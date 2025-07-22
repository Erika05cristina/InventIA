import requests
import pandas as pd
import numpy as np
import joblib
from sklearn.preprocessing import StandardScaler
from keras.layers import LSTM, Dense, Embedding, Input, Flatten, Concatenate, RepeatVector
from keras.models import Model
import os
from services.data_preparation import prepare_dataframe

MODEL_DIR = "modelos"
os.makedirs(MODEL_DIR, exist_ok=True)

SCALER_PATH = os.path.join(MODEL_DIR, "scaler.save")
MODEL_PATH = os.path.join(MODEL_DIR, "model_rnn.keras")

N_DIAS = 14
SEQ_FEATURES = ["sale_amount", "discount", "holiday_flag", "avg_temperature", "avg_humidity", "media_7d", "std_7d"]

SPRING_BOOT_URL = "http://localhost:8080/training/finished" 
def notify_backend_training_completed():
    try:
        payload = {"status": "completed"}
        response = requests.post(SPRING_BOOT_URL, json=payload)
        response.raise_for_status()
        print("✅ Notificación enviada a Spring Boot.")
    except Exception as e:
        print(f"⚠️ Error al notificar a Spring Boot: {e}")

def entrenar_modelo_global(file_path):
    df = pd.read_csv(file_path)
    df = prepare_dataframe(df)

    df = df.sort_values(["product_id", "dt"])
    scaler = StandardScaler()

    X, y, product_ids = [], [], []

    for pid in df["product_id"].unique():
        df_pid = df[df["product_id"] == pid].copy()
        if len(df_pid) < N_DIAS + 1:
            continue
        features = df_pid[SEQ_FEATURES].values
        scaler.partial_fit(features)

    for pid in df["product_id"].unique():
        df_pid = df[df["product_id"] == pid].copy()
        if len(df_pid) < N_DIAS + 1:
            continue

        features = scaler.transform(df_pid[SEQ_FEATURES].values)

        for i in range(len(features) - N_DIAS):
            X.append(features[i:i+N_DIAS])
            y.append(df_pid["sale_amount"].iloc[i + N_DIAS])
            product_ids.append(pid)

    X = np.array(X)
    y = np.array(y)
    product_ids = np.array(product_ids)

    num_products = df["product_id"].nunique()

    input_seq = Input(shape=(N_DIAS, len(SEQ_FEATURES)), name="input_seq")
    input_pid = Input(shape=(1,), name="input_pid")

    embedding = Embedding(input_dim=num_products+1, output_dim=8)(input_pid)
    embedding = Flatten()(embedding)
    repeated_embedding = RepeatVector(N_DIAS)(embedding)

    merged_input = Concatenate(axis=-1)([input_seq, repeated_embedding])

    lstm_out = LSTM(64, activation='relu')(merged_input)
    dense_out = Dense(1)(lstm_out)

    model = Model(inputs=[input_seq, input_pid], outputs=dense_out)
    model.compile(optimizer="adam", loss="mse")

    model.fit([X, product_ids], y, epochs=10, batch_size=32, verbose=1)

    joblib.dump(scaler, SCALER_PATH)
    model.save(MODEL_PATH)

    print(f"✅ Modelo entrenado y guardado en {MODEL_PATH}")
    print(f"✅ Scaler guardado en {SCALER_PATH}")

    # 🔔 Notificar al backend Spring Boot:
    notify_backend_training_completed()

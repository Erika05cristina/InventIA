import numpy as np
import pandas as pd
import joblib
from keras.models import load_model
import os
from services.data_preparation import prepare_dataframe

MODEL_DIR = "modelos"
SCALER_PATH = os.path.join(MODEL_DIR, "scaler.save")
MODEL_PATH = os.path.join(MODEL_DIR, "model_rnn.keras")

N_DIAS = 14
SEQ_FEATURES = ["sale_amount", "discount", "holiday_flag", "avg_temperature", "avg_humidity", "media_7d", "std_7d"]

def predecir_por_producto(file_path, product_id, fecha_prediccion):
    scaler = joblib.load(SCALER_PATH)
    model = load_model(MODEL_PATH)

    df = pd.read_csv(file_path)
    df = prepare_dataframe(df)
    df = df[df["product_id"] == product_id].sort_values("dt")

    if len(df) < N_DIAS:
        raise ValueError("No hay suficientes datos históricos para ese producto.")

    features = scaler.transform(df[SEQ_FEATURES].values)
    secuencia = features[-N_DIAS:]

    x_input = np.expand_dims(secuencia, axis=0)
    pid_array = np.array([[product_id]])

    pred = model.predict([x_input, pid_array])[0][0]

    return {"product_id": int(product_id), "fecha_prediccion": fecha_prediccion, "prediccion": float(pred)}


def predecir_en_lote(fecha_prediccion, file_path):
    model = load_model(MODEL_PATH)
    scaler = joblib.load(SCALER_PATH)

    df = pd.read_csv(file_path)
    df = prepare_dataframe(df)
    df_proc = df.copy()

    x_batch, ids_batch = [], []

    for pid in df["product_id"].unique():
        datos = df_proc[(df_proc["product_id"] == pid) & (df_proc["dt"] < fecha_prediccion)]
        datos = datos.sort_values("dt")

        if len(datos) < N_DIAS:
            continue

        secuencia = datos.tail(N_DIAS)[SEQ_FEATURES].values
        if np.isnan(secuencia).any():
            continue

        secuencia_scaled = scaler.transform(secuencia)
        x_batch.append(secuencia_scaled)
        ids_batch.append(pid)

    if not x_batch:
        return []

    x_batch = np.array(x_batch)
    ids_batch = np.array(ids_batch).reshape(-1, 1)

    predicciones = model.predict([x_batch, ids_batch], batch_size=128, verbose=1)

    resultados = [
        {"product_id": int(pid), "predicted_stock": float(pred)}
        for pid, pred in zip(ids_batch.flatten(), predicciones.flatten())
    ]

    return resultados
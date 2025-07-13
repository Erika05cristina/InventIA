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

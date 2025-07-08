import pandas as pd
import numpy as np
from keras.models import load_model

model = load_model("modelos/modelo_rnn.keras")

def preprocess_data(file_path):
    df = pd.read_csv(file_path)
    df = df.sort_values("fecha")
    datos = df["ventas"].values[-14:]
    max_val = datos.max() or 1
    datos_norm = datos / max_val
    return datos_norm.reshape((1, 14, 1)), max_val

def run_prediction(file_path):
    x_input, max_val = preprocess_data(file_path)
    pred_scaled = model.predict(x_input)[0][0]
    pred_real = pred_scaled * max_val
    return [{"dia": 1, "prediccion": float(pred_real)}]

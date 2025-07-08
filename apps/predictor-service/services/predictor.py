import pandas as pd
import numpy as np
from keras.models import load_model
from sklearn.preprocessing import MinMaxScaler
import os

# Cargar modelo LSTM con embeddings
model = load_model("modelos/model_lstm_embeddings.keras")

# Variables utilizadas en el modelo
SEQ_FEATURES = ["ventas", "dia", "dia_semana", "es_fin_semana", "es_feriado", "es_inicio_mes", "media_7d", "std_7d"]
N_DIAS = 14

# Simular un scaler para secuencia (si no usas persistencia)
scaler = MinMaxScaler()

def preparar_dataframe_pred(df):
    """Convierte fechas a datetime y completa columnas faltantes si es necesario"""
    df["dt"] = pd.to_datetime(df["dt"])
    for col in SEQ_FEATURES:
        if col not in df.columns:
            raise ValueError(f"Falta la columna requerida: {col}")
    return df

# -------- Predicción de prueba (endpoint /run) --------

def run_prediction(file_path):
    df = pd.read_csv(file_path)
    if "fecha" not in df.columns or "ventas" not in df.columns:
        raise ValueError("El CSV debe tener columnas: fecha, ventas")

    df["fecha"] = pd.to_datetime(df["fecha"])
    df = df.sort_values("fecha")
    datos = df["ventas"].values[-14:]
    datos_norm = datos / (datos.max() or 1)
    entrada = datos_norm.reshape((1, 14, 1))

    pred = model.predict(entrada)[0]
    return [{"dia": i + 1, "prediccion": float(p)} for i, p in enumerate(pred)]

# -------- Predicción por producto (endpoint /by-product) --------

def predecir_ventas(product_id, fecha_prediccion, file_path):
    df = pd.read_csv(file_path)
    df_proc = preparar_dataframe_pred(df)

    datos = df_proc[(df_proc["product_id"] == product_id) & (df_proc["dt"] < fecha_prediccion)]
    datos = datos.sort_values("dt")

    if len(datos) < N_DIAS:
        raise ValueError("No hay suficientes datos para ese producto")

    secuencia = datos.tail(N_DIAS)[SEQ_FEATURES].values
    if np.isnan(secuencia).any():
        raise ValueError("Datos nulos en la secuencia del producto")

    scaler.fit(secuencia)  # Escalamos por producto (temporal)
    secuencia_scaled = scaler.transform(secuencia)

    x_input = np.array([secuencia_scaled])
    pid_array = np.array([[product_id]])

    pred = model.predict([x_input, pid_array])[0][0]
    return pred

# -------- Predicción masiva (endpoint /all-products) --------

def predecir_en_lote(fecha_prediccion, file_path):
    df = pd.read_csv(file_path)
    df_proc = preparar_dataframe_pred(df)

    x_batch, ids_batch = [], []
    productos = df["product_id"].unique()

    for pid in productos:
        datos = df_proc[(df_proc["product_id"] == pid) & (df_proc["dt"] < fecha_prediccion)]
        datos = datos.sort_values("dt")

        if len(datos) < N_DIAS:
            continue

        secuencia = datos.tail(N_DIAS)[SEQ_FEATURES].values
        if np.isnan(secuencia).any():
            continue

        scaler.fit(secuencia)
        secuencia_scaled = scaler.transform(secuencia)

        x_batch.append(secuencia_scaled)
        ids_batch.append(pid)

    if not x_batch:
        raise ValueError("No se pudo generar ninguna predicción por falta de datos")

    x_batch = np.array(x_batch)
    ids_batch = np.array(ids_batch).reshape(-1, 1)

    predicciones = model.predict([x_batch, ids_batch], batch_size=128)
    predicciones = predicciones.flatten()

    resultados = [
        {"product_id": int(pid), "predicted_stock": float(pred)}
        for pid, pred in zip(ids_batch.flatten(), predicciones)
    ]

    return resultados

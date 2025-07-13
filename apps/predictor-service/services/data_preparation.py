import pandas as pd

DEFAULT_FEATURES = [
    "sale_amount",
    "discount",
    "holiday_flag",
    "avg_temperature",
    "avg_humidity",
    "media_7d",
    "std_7d"
]

def prepare_dataframe(df):
    df = df.copy()
    df["dt"] = pd.to_datetime(df["dt"])

    # Rellenar columnas faltantes con 0
    for col in DEFAULT_FEATURES:
        if col not in df.columns:
            df[col] = 0

    # Calcular media_7d y std_7d si no existen
    if "media_7d" not in df.columns:
        df["media_7d"] = df.groupby("product_id")["sale_amount"].transform(lambda x: x.rolling(7, min_periods=1).mean())

    if "std_7d" not in df.columns:
        df["std_7d"] = df.groupby("product_id")["sale_amount"].transform(lambda x: x.rolling(7, min_periods=1).std().fillna(0))

    return df

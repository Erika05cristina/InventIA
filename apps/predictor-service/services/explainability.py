import matplotlib.pyplot as plt
import numpy as np
import io
import base64
import tensorflow as tf


def generar_explicacion(prediccion, promedio_historico):
    if promedio_historico > 0:
        diff_pct = ((prediccion - promedio_historico) / promedio_historico) * 100
        if diff_pct > 0:
            return f"Se prevé un aumento de {diff_pct:.1f}% respecto al promedio histórico."
        else:
            return f"Se prevé una disminución de {abs(diff_pct):.1f}% respecto al promedio histórico."
    else:
        return "Sin datos históricos suficientes para comparación."

def compute_integrated_gradients_rnn(model, baseline_seq, input_seq, input_id, steps=50):
    alphas = np.linspace(0, 1, steps)
    integrated_grads = np.zeros_like(input_seq, dtype=np.float32)

    input_id_tensor = tf.convert_to_tensor(input_id, dtype=tf.int32)

    for alpha in alphas:
        interpolated_seq = baseline_seq + alpha * (input_seq - baseline_seq)
        interpolated_seq = np.expand_dims(interpolated_seq, axis=0)
        interpolated_seq = tf.convert_to_tensor(interpolated_seq, dtype=tf.float32)

        with tf.GradientTape() as tape:
            tape.watch(interpolated_seq)
            preds = model([interpolated_seq, input_id_tensor], training=False)

        grads = tape.gradient(preds, interpolated_seq)[0]
        integrated_grads += grads.numpy()

    integrated_grads = (input_seq - baseline_seq) * integrated_grads / steps
    return integrated_grads

def explicar_prediccion_producto(idx, model, x_eval, product_ids_eval, compute_integrated_gradients_rnn):
    sample_seq = x_eval[idx]
    sample_id = np.array([[product_ids_eval[idx]]])
    baseline_seq = np.zeros_like(sample_seq)

    pred = model.predict([np.expand_dims(sample_seq, axis=0), sample_id])[0][0]
    igrads_seq = compute_integrated_gradients_rnn(model, baseline_seq, sample_seq, sample_id)
    importance_total = np.sum(np.abs(igrads_seq), axis=0)

    feature_names = [
        "sale_amount",
        "discount",
        "holiday_flag",
        "avg_temperature",
        "avg_humidity",
        "media_7d",
        "std_7d"
    ]

    # 🔔 Diccionario para traducir nombres a español
    feature_translation = {
        "sale_amount": "Cantidad vendida",
        "discount": "Descuentos aplicados",
        "holiday_flag": "Días festivos",
        "avg_temperature": "Temperatura promedio",
        "avg_humidity": "Humedad ambiente",
        "media_7d": "Promedio últimos 7 días",
        "std_7d": "Variabilidad últimos 7 días"
    }

    # Traducción dinámica
    translated_feature_names = [feature_translation.get(f, f) for f in feature_names]

    importancia_ordenada = sorted(zip(translated_feature_names, importance_total), key=lambda x: abs(x[1]), reverse=True)

    fig, ax = plt.subplots(figsize=(10, 4))
    bars = ax.bar(
        translated_feature_names,
        importance_total,
        color="#163C8F",
        edgecolor='black',
        linewidth=1,
        alpha=0.85
    )

    ax.set_title(f"Importancia de variables - Producto {sample_id[0][0]}", fontsize=14, fontweight='bold')
    ax.set_ylabel("Importancia acumulada", fontsize=12)
    ax.tick_params(axis='x', labelrotation=45, labelsize=10)
    ax.tick_params(axis='y', labelsize=10)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)
    ax.yaxis.grid(True, linestyle='--', alpha=0.6)

    for bar in bars:
        height = bar.get_height()
        ax.annotate(f"{height:.2f}",
                    xy=(bar.get_x() + bar.get_width() / 2, height),
                    xytext=(0, 3),
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=8)

    plt.tight_layout()

    buf = io.BytesIO()
    plt.savefig(buf, format='png', dpi=150, bbox_inches='tight', transparent=True)
    plt.close(fig)
    buf.seek(0)

    img_base64 = base64.b64encode(buf.read()).decode('utf-8')
    img_data_uri = f"data:image/png;base64,{img_base64}"

    return {
        "producto_id": int(sample_id[0][0]),
        "prediccion": float(pred),
        "variables_importantes": importancia_ordenada[:5],
        "grafica_explicabilidad_base64": img_data_uri
    }

import matplotlib
matplotlib.use('Agg')
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

    input_id_tensor = tf.convert_to_tensor(input_id, dtype=tf.int32)  # 👈 FIX: convertir input_id una vez

    for alpha in alphas:
        interpolated_seq = baseline_seq + alpha * (input_seq - baseline_seq)
        interpolated_seq = np.expand_dims(interpolated_seq, axis=0)
        interpolated_seq = tf.convert_to_tensor(interpolated_seq, dtype=tf.float32)  # 👈 FIX ya hecho antes

        with tf.GradientTape() as tape:
            tape.watch(interpolated_seq)
            preds = model([interpolated_seq, input_id_tensor], training=False)  # 👈 Ahora input_id también es tensor

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

    importancia_ordenada = sorted(zip(feature_names, importance_total), key=lambda x: abs(x[1]), reverse=True)

    fig, ax = plt.subplots(figsize=(10, 4))
    ax.bar(range(len(importance_total)), importance_total)
    ax.set_xticks(range(len(importance_total)))
    ax.set_xticklabels(feature_names, rotation=45)
    ax.set_title(f"Importancia de variables - Producto ID {sample_id[0][0]}")
    ax.set_ylabel("Importancia acumulada")
    ax.grid(True)
    plt.tight_layout()

    buf = io.BytesIO()
    plt.savefig(buf, format='png')
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

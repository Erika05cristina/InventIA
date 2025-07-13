def generar_explicacion(prediccion, promedio_historico):
    if prediccion > promedio_historico:
        diff_pct = ((prediccion - promedio_historico) / promedio_historico) * 100
        texto = f"Se prevé un aumento de {diff_pct:.1f}% respecto al promedio histórico."
    else:
        diff_pct = ((promedio_historico - prediccion) / promedio_historico) * 100
        texto = f"Se prevé una disminución de {diff_pct:.1f}% respecto al promedio histórico."

    return texto

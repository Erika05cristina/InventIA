import requests
import re
import dateparser
from dateparser.search import search_dates


def extraer_fecha(pregunta: str) -> str | None:
    """
    Extrae una fecha en formato YYYY-MM-DD desde una pregunta en español.
    """
    resultado = search_dates(pregunta, languages=["es"])
    if resultado:
        return resultado[0][1].strftime("%Y-%m-%d")

    fecha_directa = dateparser.parse(pregunta, languages=["es"])
    if fecha_directa:
        return fecha_directa.strftime("%Y-%m-%d")

    return None


def obtener_productos() -> list[dict]:
    """
    Consulta al backend la lista de productos disponibles.
    """
    try:
        response = requests.get("http://localhost:8080/products/all")
        if response.status_code == 200:
            return response.json()
    except Exception as e:
        print(f"❗ Error al obtener productos: {e}")
    return []

def obtener_nombre_producto(product_id: int) -> str | None:
    try:
        url = f"http://localhost:8080/products/by-id?productId={product_id}"
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            return data.get("nombre", None)
    except Exception:
        pass
    return None


def buscar_product_id_por_nombre(nombre_producto: str) -> int | None:
    """
    Intenta encontrar el ID del producto a partir del nombre (incluso parcial).
    """
    productos = obtener_productos()
    nombre_normalizado = nombre_producto.lower()

    for p in productos:
        if nombre_normalizado in p["nombre"].lower():
            return p["id"]
    return None

def consultar_estadisticas(pregunta: str) -> dict:
    """
    Consulta estadísticas generales de stock si el usuario da solo la fecha.
    Agrega nombres de productos en lugar de solo los IDs.
    """
    fecha_str = extraer_fecha(pregunta)

    if not fecha_str:
        return {
            "mensaje": "❗ No se detectó ninguna fecha en tu pregunta. Por favor indícala explícitamente, como 'ayer' o 'el 25 de julio'."
        }

    url = f"http://localhost:8080/dashboard/statistics?fecha={fecha_str}&tipo=grupo"

    try:
        response = requests.get(url)
        if response.status_code == 404:
            return {"mensaje": f"No se encontró ninguna predicción para la fecha {fecha_str}."}
        if response.status_code != 200:
            return {"mensaje": f"Ocurrió un error al consultar las estadísticas para {fecha_str} (código {response.status_code})."}

        data = response.json()

        # Obtener nombres para los productos con mayor y menor demanda
        id_mayor = data.get("productoMayorDemandaId")
        id_menor = data.get("productoMenorDemandaId")
        nombre_mayor = obtener_nombre_producto(id_mayor) or f"Producto ID {id_mayor}"
        nombre_menor = obtener_nombre_producto(id_menor) or f"Producto ID {id_menor}"

        # Agregar los nombres al diccionario de respuesta
        data["productoMayorDemandaNombre"] = nombre_mayor
        data["productoMenorDemandaNombre"] = nombre_menor

        return data

    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión con el backend: {str(e)}"}


def consultar_explicacion(pregunta: str) -> dict:
    # 1️⃣ Intentar detectar número de producto explícito
    producto_match = re.search(r"(producto\s*(número|nro|num|#)?\s*)?(\d{1,5})", pregunta, re.IGNORECASE)
    product_id = None

    if producto_match:
        product_id = int(producto_match.group(3))
    else:
        # 2️⃣ Si no se detecta número, intentar buscar por nombre
        productos = obtener_productos()
        for producto in productos:
            nombre = producto["nombre"].lower()
            if nombre in pregunta.lower():
                product_id = producto["id"]
                break

    if not product_id:
        return {"mensaje": "❗ No se identificó el producto en tu pregunta. Puedes decir algo como 'producto 267' o el nombre exacto."}

    # 3️⃣ Obtener la fecha
    fecha_str = extraer_fecha(pregunta)
    if not fecha_str:
        return {"mensaje": "❗ No se encontró una fecha en tu pregunta. Ej: 'el 25 de julio'"}

    # 4️⃣ Llamar al backend FastAPI
    url = "http://localhost:8000/predict/by-product"
    payload = {
        "product_id": product_id,
        "fecha_prediccion": fecha_str
    }

    try:
        response = requests.post(url, json=payload)
        if response.status_code != 200:
            return {"mensaje": f"Error {response.status_code} al consultar la explicación."}

        data = response.json()

        # ❌ Eliminar imagen si existe
        if "explicacion_avanzada" in data and "grafica_explicabilidad_base64" in data["explicacion_avanzada"]:
            data["explicacion_avanzada"].pop("grafica_explicabilidad_base64")

        # ✅ Obtener y agregar nombre del producto
        nombre = obtener_nombre_producto(product_id)
        if nombre:
            data["nombre_producto"] = nombre
            if "explicacion_simple" in data and f"producto {product_id}" in data["explicacion_simple"]:
                data["explicacion_simple"] = data["explicacion_simple"].replace(f"producto {product_id}", f"{nombre}")

        return data

    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión al backend: {str(e)}"}

import typing
from starlette.datastructures import UploadFile, FormData
from starlette.formparsers import MultiPartParser

# Esta clase hereda del parser original de Starlette y solo cambia el tamaño máximo.
class MaxSizeEnforcingMultiPartParser(MultiPartParser):
    def __in_init__(
        self,
        headers: typing.Mapping[str, str],
        stream: typing.AsyncGenerator[bytes, None],
        max_size: int = 2**20,  # El valor por defecto es 1MB (2**20 bytes)
        # ... otros parámetros
    ) -> None:
        # Aquí no hacemos nada, solo es para mostrar la firma original.
        # La magia sucede en la clase de middleware.
        pass

# Este es el middleware que realmente aplicarás a tu aplicación.
class MultipartRequestMiddleware:
    def __init__(self, app, max_upload_size_mb: int = 100):
        self.app = app
        # Convertimos MB a bytes. 100MB en este caso.
        self.max_upload_size = max_upload_size_mb * 1024 * 1024

    async def __call__(self, scope, receive, send):
        if scope["type"] == "http":
            # Sobrescribimos el parser solo para las solicitudes multipart/form-data
            content_type_header = ""
            for header_key, header_value in scope["headers"]:
                if header_key.decode("latin-1") == "content-type":
                    content_type_header = header_value.decode("latin-1")
                    break

            if "multipart/form-data" in content_type_header:
                # Creamos una instancia del parser con nuestro tamaño máximo personalizado.
                MultiPartParser.max_size = self.max_upload_size

        await self.app(scope, receive, send)

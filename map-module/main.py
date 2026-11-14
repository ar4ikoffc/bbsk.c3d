"""Main FastAPI application module."""

from fastapi import FastAPI
from fastapi.responses import FileResponse

app = FastAPI(title="Routing & Points Management")

# Include API routes from routers module
from routers import router
app.include_router(router)


@app.get("/")
async def root():
    return FileResponse('map_interface.html')
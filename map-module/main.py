"""Main FastAPI application module."""

from fastapi import FastAPI

app = FastAPI(title="Routing & Points Management")

# Include API routes from routers module
from routers import router
app.include_router(router)
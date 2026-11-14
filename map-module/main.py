import fastapi
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import List, Optional
import json
import os
import math
from enum import Enum

import sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from routers import (
    PointType,
    PointCreate,
    PointUpdate,
    PointResponse,
    get_path_length_m,
    haversine_dist_m,
    shortest_path_cache,
    distribute_routes,
    plan_reequipment,
    ReequipmentRequest,
    RouteDistributionResponse,
    RouteDistributionPoint,
    RouteDistributionRequest,
    RoutePoint,
    RouteRequest,
    RouteResult,
)

app = FastAPI(title="Routing & Points Management")

MAP_DIR = os.path.dirname(os.path.abspath(__file__))

points_store = {
    "points": {}
}

# ============================================================
# CRUD ENDPOINTS
# ============================================================

@app.get("/")
async def root():
    return FileResponse('map_interface.html')


@app.get("/map/info")
async def map_info():
    return {
        "city": "Yekaterinburg",
        "format": "Points with types and routing",
        "points_count": len(points_store["points"])
    }


@app.post("/points", response_model=PointResponse)
async def create_point(point: PointCreate):
    if point.id in points_store["points"]:
        raise HTTPException(400, detail="Point ID already exists")
    points_store["points"][point.id] = {
        "id": point.id, "name": point.name,
        "point_type": point.point_type, "lat": 0.0, "lng": 0.0
    }
    return points_store["points"][point.id]


@app.get("/points", response_model=List[PointResponse])
async def get_all_points():
    return list(points_store["points"].values())


@app.get("/points/{point_id}", response_model=PointResponse)
async def get_point(point_id: str):
    if point_id not in points_store["points"]:
        raise HTTPException(404, detail="Point not found")
    return points_store["points"][point_id]


@app.put("/points/{point_id}", response_model=PointResponse)
async def update_point(point_id: str, point: PointUpdate):
    if point_id not in points_store["points"]:
        raise HTTPException(404, detail="Point not found")
    p = points_store["points"][point_id]
    if point.name is not None:
        p["name"] = point.name
    if point.point_type is not None:
        p["point_type"] = point.point_type
    return p


@app.delete("/points/{point_id}")
async def delete_point(point_id: str):
    if point_id not in points_store["points"]:
        raise HTTPException(404, detail="Point not found")
    del points_store["points"][point_id]
    return {"status": "deleted"}


# ============================================================
# ROUTE ENDPOINTS
# ============================================================

@app.post("/routes/distribute", response_model=RouteDistributionResponse)
async def distribute_routes_endpoint(request: RouteDistributionRequest):
    return distribute_routes(request, points_store)


@app.post("/routes/reequipment")
async def plan_reequipment_endpoint(request: ReequipmentRequest):
    return plan_reequipment(request, points_store)


# ============================================================
# STATIC FILES
# ============================================================

@app.get("/static/*")
async def static_files(path: str):
    file_path = os.path.join(MAP_DIR, path.lstrip("/"))
    if os.path.exists(file_path):
        return FileResponse(file_path)
    raise HTTPException(status_code=404, detail="File not found")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
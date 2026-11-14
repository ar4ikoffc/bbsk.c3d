import fastapi
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import List, Optional, TYPE_CHECKING
import json
import os
import math
from enum import Enum

# Optional OSM imports - only available if osmnx is installed
try:
    import osmnx as ox
    import networkx as nx

    # Build OSM graph for Yekaterinburg at startup
    G = ox.graph_from_place('Yekaterinburg, Russia', network_type='drive')
    G_projected = ox.project_graph(G)
    HAS_OSMNX = True
except ImportError:
    HAS_OSMNX = False

# TYPE CHECKING ONLY - not executed at runtime
if TYPE_CHECKING:
    from osmnx import Graph  # type: ignore

# ============================================================
# APP & STORAGE SETUP
# ============================================================

app = FastAPI(title="Routing & Points Management")

MAP_DIR = os.path.dirname(os.path.abspath(__file__))

points_store = {
    "points": {}
}

# ============================================================
# GEO HELPERS & CACHE
# ============================================================

# Module-level cache for shortest path lengths
shortest_path_cache = {}

def haversine_dist_m(lat1, lng1, lat2, lng2) -> float:
    """Calculate approximate distance in meters between two coordinates."""
    R = 6371000  # Earth radius in meters
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lng2 - lng1)
    a = math.sin(delta_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c


def get_path_length_m(start_lat: float, start_lng: float, end_lat: float, end_lng: float) -> float:
    """Get shortest path length in meters using OSM if available, else haversine."""
    cache_key = f"{start_lat}_{start_lng}_{end_lat}_{end_lng}"
    if cache_key in shortest_path_cache:
        return shortest_path_cache[cache_key]

    if HAS_OSMNX:
        try:
            import networkx as nx

            start_node = ox.nearest_nodes(G_projected, X=start_lng, Y=start_lat)
            end_node = ox.nearest_nodes(G_projected, X=end_lng, Y=end_lat)
            dist = nx.shortest_path_length(G_projected, source=start_node, target=end_node, weight='length')
            shortest_path_cache[cache_key] = dist
            return dist
        except Exception:
            pass  # Fall through to haversine

    # Haversine fallback
    R = 6371000
    phi1 = math.radians(start_lat)
    phi2 = math.radians(end_lat)
    delta_phi = math.radians(end_lat - start_lat)
    delta_lambda = math.radians(lng2 - start_lng)
    a = math.sin(delta_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    dist = R * c
    shortest_path_cache[cache_key] = dist
    return dist


get_path_length_m._cache = {}


# ============================================================
# TYPES & MODELS
# ============================================================

class PointType(str, Enum):
    SERVICE_CENTER = "service_center"
    ABONENT = "abonent"


class PointCreate(BaseModel):
    id: str
    name: str
    point_type: PointType = PointType.ABONENT


class PointUpdate(BaseModel):
    name: Optional[str] = None
    point_type: Optional[PointType] = None


class PointResponse(BaseModel):
    id: str
    name: str
    point_type: PointType
    lat: float
    lng: float


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
# ROUTE MODELS
# ============================================================

class RoutePoint(BaseModel):
    lat: float
    lng: float
    name: str


class RouteDistributionPoint(BaseModel):
    index: int
    lat: float
    lng: float
    name: str


class RouteDistributionRequest(BaseModel):
    start_lat: float
    start_lng: float
    points: List[RoutePoint]
    n_routes: int


class RouteDistributionResponse(BaseModel):
    routes: List[List[int]]
    total_distances_km: List[float]
    route_details: List[List[dict]]


class RouteRequest(BaseModel):
    start_point_id: str
    abonent_ids: List[str]
    service_time_minutes: int = 15


class RouteResult(BaseModel):
    route_id: int
    path_sequence: List[str]
    total_distance_m: float
    total_time_minutes: float
    details: List[dict]


# ============================================================
# ROUTE DISTRIBUTION
# ============================================================

@app.post("/routes/distribute", response_model=RouteDistributionResponse)
async def distribute_routes(request: RouteDistributionRequest):
    n_points = len(request.points)
    n_routes = request.n_routes
    if n_routes < 1:
        raise HTTPException(400, detail="n_routes must be >= 1")
    if n_points == 0:
        raise HTTPException(400, detail="No points provided")

    # Build distance matrix using get_path_length_m
    n_total = n_points + 1
    dist_matrix = [[0.0] * n_total for _ in range(n_total)]

    for i, point in enumerate(request.points):
        dist_matrix[0][i + 1] = get_path_length_m(
            request.start_lat, request.start_lng, point.lat, point.lng)
        dist_matrix[i + 1][0] = dist_matrix[0][i + 1]

    for i in range(n_points):
        for j in range(i + 1, n_points):
            d = get_path_length_m(
                request.points[i].lat, request.points[i].lng,
                request.points[j].lat, request.points[j].lng)
            dist_matrix[i + 1][j + 1] = d
            dist_matrix[j + 1][i + 1] = d

    # Round-robin distribution
    routes = [[] for _ in range(n_routes)]
    point_indices = list(range(n_points))
    for i, pt_idx in enumerate(point_indices):
        routes[i % n_routes].append(pt_idx)

    # Calculate route distances
    route_distances = [0.0] * n_routes
    route_details = [[] for _ in range(n_routes)]

    for r in range(n_routes):
        route_total = 0.0
        for i in range(len(routes[r]) - 1):
            from_idx = routes[r][i] if routes[r][i] > 0 else 0
            to_idx = routes[r][i + 1] if routes[r][i + 1] > 0 else 0
            if from_idx > 0 and to_idx > 0:
                d = get_path_length_m(
                    request.points[from_idx - 1].lat, request.points[from_idx - 1].lng,
                    request.points[to_idx - 1].lat, request.points[to_idx - 1].lng)
                route_total += d
        route_distances[r] = route_total / 1000  # km
        route_details[r] = [
            {"index": idx, "lat": request.points[idx].lat, "lng": request.points[idx].lng, "name": request.points[idx].name}
            for idx in routes[r]
        ]

    return {
        "routes": routes,
        "total_distances_km": route_distances,
        "route_details": route_details
    }


# ============================================================
# REEQUIPMENT LOGIC
# ============================================================

class ReequipmentRequest(BaseModel):
    brigade_id: str
    current_equipment_count: int
    required_equipment_count: int
    start_point_id: str


@app.post("/routes/reequipment")
async def plan_reequipment(request: ReequipmentRequest):
    if request.start_point_id not in points_store["points"]:
        raise HTTPException(404, detail="Start point not found")
    if request.current_equipment_count >= request.required_equipment_count:
        return {
            "needs_reequipment": False,
            "message": "Brigade has sufficient equipment",
            "route": None
        }
    return {
        "needs_reequipment": True,
        "equipment_deficit": request.required_equipment_count - request.current_equipment_count,
        "start_point_id": request.start_point_id,
        "message": "Brigade needs reequipment from service center",
        "route_suggestion": {
            "description": "Brigade should go to service center, pick up equipment, and return",
            "start_point_id": request.start_point_id,
            "current_equipment": request.current_equipment_count,
            "required_equipment": request.required_equipment_count
        }
    }


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
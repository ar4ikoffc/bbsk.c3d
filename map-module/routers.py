from pydantic import BaseModel
from typing import List
from enum import Enum

def haversine_dist_m(lat1, lng1, lat2, lng2) -> float:
    """Calculate approximate distance in meters between two coordinates."""
    import math
    R = 6371000
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lng2 - lng1)
    a = math.sin(delta_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c


shortest_path_cache = {}


def get_path_length_m(start_lat: float, start_lng: float, end_lat: float, end_lng: float) -> float:
    """Get shortest path length in meters using haversine fallback."""
    cache_key = f"{start_lat}_{start_lng}_{end_lat}_{end_lng}"
    if cache_key in shortest_path_cache:
        return shortest_path_cache[cache_key]

    import math
    R = 6371000
    phi1 = math.radians(start_lat)
    phi2 = math.radians(end_lat)
    delta_phi = math.radians(end_lat - start_lat)
    delta_lambda = math.radians(end_lng - start_lng)
    a = math.sin(delta_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    dist = R * c
    shortest_path_cache[cache_key] = dist
    return dist


get_path_length_m._cache = {}


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


class ReequipmentRequest(BaseModel):
    brigade_id: str
    current_equipment_count: int
    required_equipment_count: int
    start_point_id: str


def distribute_routes(request: RouteDistributionRequest, points_store):
    """Distribute points into routes using round-robin distribution."""
    n_points = len(request.points)
    n_routes = request.n_routes
    if n_routes < 1:
        from fastapi import HTTPException
        raise HTTPException(400, detail="n_routes must be >= 1")
    if n_points == 0:
        from fastapi import HTTPException
        raise HTTPException(400, detail="No points provided")

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

    routes = [[] for _ in range(n_routes)]
    point_indices = list(range(n_points))
    for i, pt_idx in enumerate(point_indices):
        routes[i % n_routes].append(pt_idx)

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


def plan_reequipment(request: ReequipmentRequest, points_store):
    """Check if reequipment is needed."""
    if request.start_point_id not in points_store.get("points", {}):
        from fastapi import HTTPException
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
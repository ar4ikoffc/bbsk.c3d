# Dockerfile for OSRM backend based on PBF file
FROM jostmey/osrm-car:latest AS osrm-build

# Install OSRM toolchain
RUN apt-get update && apt-get install -y wget bzip2 && rm -rf /var/lib/apt-get/cache/*

# Copy PBF file from build context
COPY osrm/ural-fed-district-261113.osm.pbf /data/

# Extract and prepare the OSRM map
RUN osrm-extract /data/ural-fed-district-261113.osm.pbf

# Generate the OSRM file
RUN osrm-contract /data/ural-fed-district-261113.osm

# Final stage: runtime OSRM server
FROM jostmey/osrm-car:latest

# Copy the generated OSRM file from build stage
COPY --from=osrm-build /data/ural-fed-district-261113.osrm /data/

# Expose the OSRM routing port
EXPOSE 5000

# Start the OSRM router
CMD ["/osrm-routed", "/data/ural-fed-district-261113.osrm"]
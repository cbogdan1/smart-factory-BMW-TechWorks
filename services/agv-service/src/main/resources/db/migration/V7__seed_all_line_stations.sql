-- Completes the factory graph so every production line/stage combination that Assembly can request
-- material for is actually reachable by an AGV.
--
-- Background: Assembly derives the delivery target node as
--     <productionLine>-<currentStage>-STATION      (e.g. LINE-1-INTERIOR-STATION)
-- and Planning assigns the least loaded line out of LINE-1, LINE-2 and LINE-3.
-- V5 only seeded LINE-2 BODY/PAINT/POWERTRAIN stations, so any vehicle planned on LINE-1 or LINE-3,
-- and every vehicle reaching the INTERIOR stage, failed routing with NoRouteException and therefore
-- never produced a material-delivered event.
--
-- This migration adds the missing stations for all three lines and connects them to the existing
-- junction backbone. Edges are directed (the router only follows source -> target), so a return edge
-- towards the quality area is added as well to keep the graph consistent with V5.

INSERT INTO node (node_id, name, type, created_date)
VALUES
    -- LINE-1
    ('LINE-1-BODY-STATION', 'Line 1 Body Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-1-PAINT-STATION', 'Line 1 Paint Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-1-INTERIOR-STATION', 'Line 1 Interior Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-1-POWERTRAIN-STATION', 'Line 1 Powertrain Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-1-FINAL-STATION', 'Line 1 Final Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    -- LINE-2 (BODY / PAINT / POWERTRAIN already seeded in V5)
    ('LINE-2-INTERIOR-STATION', 'Line 2 Interior Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-2-FINAL-STATION', 'Line 2 Final Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    -- LINE-3
    ('LINE-3-BODY-STATION', 'Line 3 Body Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-3-PAINT-STATION', 'Line 3 Paint Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-3-INTERIOR-STATION', 'Line 3 Interior Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-3-POWERTRAIN-STATION', 'Line 3 Powertrain Station', 'LINE_STATION', CURRENT_TIMESTAMP),
    ('LINE-3-FINAL-STATION', 'Line 3 Final Station', 'LINE_STATION', CURRENT_TIMESTAMP);

INSERT INTO edge (edge_id, source_node_id, target_node_id, distance_meters, created_date)
VALUES
    -- LINE-1 hangs off BUFFER-A / JUNCTION-1
    ('EDGE-101', 'BUFFER-A', 'LINE-1-BODY-STATION', 11, CURRENT_TIMESTAMP),
    ('EDGE-102', 'JUNCTION-1', 'LINE-1-PAINT-STATION', 19, CURRENT_TIMESTAMP),
    ('EDGE-103', 'JUNCTION-1', 'LINE-1-INTERIOR-STATION', 23, CURRENT_TIMESTAMP),
    ('EDGE-104', 'JUNCTION-1', 'LINE-1-POWERTRAIN-STATION', 26, CURRENT_TIMESTAMP),
    ('EDGE-105', 'JUNCTION-1', 'LINE-1-FINAL-STATION', 30, CURRENT_TIMESTAMP),
    ('EDGE-106', 'LINE-1-FINAL-STATION', 'QUALITY-AREA', 35, CURRENT_TIMESTAMP),
    -- LINE-2 additions
    ('EDGE-107', 'JUNCTION-3', 'LINE-2-INTERIOR-STATION', 14, CURRENT_TIMESTAMP),
    ('EDGE-108', 'JUNCTION-3', 'LINE-2-FINAL-STATION', 20, CURRENT_TIMESTAMP),
    ('EDGE-109', 'LINE-2-FINAL-STATION', 'QUALITY-AREA', 22, CURRENT_TIMESTAMP),
    -- LINE-3 hangs off BUFFER-B / JUNCTION-2
    ('EDGE-110', 'BUFFER-B', 'LINE-3-BODY-STATION', 13, CURRENT_TIMESTAMP),
    ('EDGE-111', 'JUNCTION-2', 'LINE-3-PAINT-STATION', 21, CURRENT_TIMESTAMP),
    ('EDGE-112', 'JUNCTION-2', 'LINE-3-INTERIOR-STATION', 24, CURRENT_TIMESTAMP),
    ('EDGE-113', 'JUNCTION-2', 'LINE-3-POWERTRAIN-STATION', 27, CURRENT_TIMESTAMP),
    ('EDGE-114', 'JUNCTION-2', 'LINE-3-FINAL-STATION', 31, CURRENT_TIMESTAMP),
    ('EDGE-115', 'LINE-3-FINAL-STATION', 'QUALITY-AREA', 33, CURRENT_TIMESTAMP);


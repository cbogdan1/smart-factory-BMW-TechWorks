package com.smartfactory.common;

public final class EndpointPaths {

    public static final String API_V1 = "/api/v1";
    public static final String DEFECTS = API_V1 + "/defects";
    public static final String INSPECTIONS = API_V1 + "/inspections";
    public static final String INVENTORY = API_V1 + "/inventory";
    public static final String VEHICLES = API_V1 + "/vehicles";
    public static final String ORDERS = API_V1 + "/orders";
    public static final String NODES = API_V1 + "/nodes";
    public static final String EDGES = API_V1 + "/edges";
    public static final String AGVS = API_V1 + "/agvs";
    public static final String GRAPH = API_V1 + "/graphShow";
    public static final String ROUTES = API_V1 + "/routes";
    public static final String ASSIGNMENTS = API_V1 + "/assignments";
    public static final String SUPPLIERPART = API_V1 + "/supplier-parts";
    public static final String PARTS = API_V1 + "/parts";
    public static final String MATERIAL_DEMANDS = API_V1 + "/material-demands";
    public static final String PURCHASEORDER = API_V1 + "/purchase-order";
    public static final String SUPPLIERS = API_V1 + "/suppliers";
    public static final String TWINS = API_V1 + "/twins";
    public static final String TIMELINE = API_V1 + "/timeline";
    public static final String DASHBOARD_KPIS = API_V1 + "/dashboard/kpis";

    public static final String PLANS = API_V1 + "/plans";
    public static final String EVENTS = API_V1 + "/events";
    public static final String ANALYTICS_SUMMARY = API_V1 + "/analytics/summary";
    public static final String SIMULATEDELIVERY = API_V1 + "/simulate-delivery";

    private EndpointPaths() {
    }
}
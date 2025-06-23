package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.data.MaterializedView;
import de.tub.dima.policyliner.database.data.TableInformation;
import de.tub.dima.policyliner.database.data.UserDefinedFunction;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/data")
public class DataDBResource {

    private final DataDBService dataDBService;

    public DataDBResource(DataDBService dataDBService) {
        this.dataDBService = dataDBService;
    }

    @GET
    @Path("/tables")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDataTables() {
        List<TableInformation> tables = dataDBService.getTables();
        return Response.ok(tables).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/udfunctions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserDefinedFunctions() {
        List<UserDefinedFunction> userDefinedFunctions = dataDBService.getUserDefinedFunctions();
        return Response.ok(userDefinedFunctions).build();
    }

    @GET
    @Path("/views")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMaterializedViews() {
        List<MaterializedView> materializedViews = dataDBService.getMaterializedViews();
        return Response.ok(materializedViews).build();
    }

}

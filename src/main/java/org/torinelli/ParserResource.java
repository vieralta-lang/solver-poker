package org.torinelli;


import org.torinelli.api.dto.HandMetaDataResponse;
import org.torinelli.domain.HandMetaData;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/replay")
public class ParserResource {

    @Inject
    ParserCommand command;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public HandMetaDataResponse getHand(String hand) {
        HandMetaData metaData = command.execute(hand);
        return HandMetaDataResponse.fromDomain(metaData);
    }
}

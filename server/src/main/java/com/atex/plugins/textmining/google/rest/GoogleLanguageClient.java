package com.atex.plugins.textmining.google.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.language.v1.AnnotateTextRequest;
import com.google.cloud.language.v1.AnnotateTextResponse;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.common.base.Strings;


public final class GoogleLanguageClient implements GoogleClient {

    public GoogleResponse annotate(String text) throws IOException {

        if (Strings.isNullOrEmpty(text)) {
            throw new IllegalArgumentException("Invalid, content is empty.");
        }
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            com.google.cloud.language.v1.Document doc = com.google.cloud.language.v1.Document.newBuilder().setContent(text).setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT).build();
            AnnotateTextResponse annotateTextResponse = language.annotateText(doc, AnnotateTextRequest.Features.newBuilder()
                    .setExtractEntities(true)
                    .setClassifyText(true)
                    .build());
             return parse(annotateTextResponse);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private GoogleResponse parse(AnnotateTextResponse response){
        GoogleResponse googleResponse = new GoogleResponse();
        try {
            googleResponse.setMetadata(getMetadataInfo(response));
            googleResponse.setEntities(response.getEntitiesList());
            googleResponse.setClassificationCategories(response.getCategoriesList());
            return googleResponse;
        } catch(Exception e){
            throw e;
        }
    }

    private static Map getMetadataInfo(AnnotateTextResponse response){
        Map<Entity.Type, String> dimensionMap =  getDimensionMap();
        Map<String, String> metadata = new HashMap<>();
        for (Entity entity : response.getEntitiesList()) {
            String dimension = dimensionMap.get (entity.getType());
            if(entity.getSalience() > 0.04){
                metadata.put(entity.getName(), dimension);
                System.out.println(entity.getName() + " " + dimension);
            }
        }
        return metadata;
    }

    private static Map getDimensionMap(){
        Map<Entity.Type, String> dimensionMap = new HashMap<>();
        dimensionMap.put (Entity.Type.PERSON, "dimension.Person");
        dimensionMap.put (Entity.Type.LOCATION, "dimension.Location");
        dimensionMap.put (Entity.Type.ORGANIZATION, "dimension.Organisation");
        dimensionMap.put (Entity.Type.OTHER, "dimension.Tag");
        return dimensionMap;
    }
}

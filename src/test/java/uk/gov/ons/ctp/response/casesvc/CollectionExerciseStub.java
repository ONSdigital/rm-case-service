package uk.gov.ons.ctp.response.casesvc;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;


import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class CollectionExerciseStub {

    private static final Logger log = LoggerFactory.getLogger(CollectionExerciseStub.class);

    public void getCollectionExerciseStub() throws Exception{
        log.info("Stubbing collection exercise endpoint");



        stubFor(get(urlPathEqualTo("/collectionexercises/2eb78e81-ae85-45d2-a5ae-6d54974d8bc7"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("\"[{id:2eb78e81-ae85-45d2-a5ae-6d54974d8bc7, caseTypes: [{\n" +
                                        "        \"actionPlanId\": \"1cbea564-b989-4458-9db2-8a4db65fb82e\",\n" +
                                        "        \"sampleUnitType\": \"B\"\n" +
                                        "      },\n" +
                                        "      {\n" +
                                        "        \"actionPlanId\": \"50849aa2-439b-4849-9603-7e237662f52c\",\n" +
                                        "        \"sampleUnitType\": \"BI\"\n" +
                                        "      }], exerciseRef:201801, surveyId:cb8accda-6118-4d3b-85a3-149e28960c54  }]")
                                .withStatus(200)));
    }

}

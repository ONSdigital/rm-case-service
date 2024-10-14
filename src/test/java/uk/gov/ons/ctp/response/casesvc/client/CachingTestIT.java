package uk.gov.ons.ctp.response.casesvc.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;

@ContextConfiguration
@ActiveProfiles("cachingtest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:/application-test.yml")
public class CachingTestIT {

  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  private UUID collectionExerciseId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c55");

  @Test
  public void testCache() throws InterruptedException {

    String expectedSurveyId = UUID.randomUUID().toString();

    StubMapping stubMapping = createCollexStub(expectedSurveyId);
    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedSurveyId, collex.getSurveyId());

    String expectedSecondSurveyId = UUID.randomUUID().toString();
    removeStub(stubMapping);
    stubMapping = createCollexStub(expectedSecondSurveyId);
    CollectionExerciseDTO collex1 =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedSurveyId, collex1.getSurveyId());
    assertNotEquals(expectedSecondSurveyId, collex1.getSurveyId());

    String expectedThirdSurveyId = UUID.randomUUID().toString();
    removeStub(stubMapping);
    stubMapping = createCollexStub(expectedThirdSurveyId);
    CollectionExerciseDTO collex2 =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedSurveyId, collex2.getSurveyId());
    assertNotEquals(expectedThirdSurveyId, collex2.getSurveyId());
    removeStub(stubMapping);
  }

  @Test
  public void testCacheEvict() throws InterruptedException {

    String expectedSurveyId = UUID.randomUUID().toString();

    StubMapping stubMapping = createCollexStub(expectedSurveyId);
    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedSurveyId, collex.getSurveyId());

    String expectedSecondSurveyId = UUID.randomUUID().toString();
    removeStub(stubMapping);
    stubMapping = createCollexStub(expectedSecondSurveyId);
    CollectionExerciseDTO collex1 =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedSurveyId, collex1.getSurveyId());
    assertNotEquals(expectedSecondSurveyId, collex1.getSurveyId());
    Thread.sleep(61000);

    String expectedThirdSurveyId = UUID.randomUUID().toString();
    removeStub(stubMapping);
    stubMapping = createCollexStub(expectedThirdSurveyId);
    CollectionExerciseDTO collex2 =
        collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
    assertEquals(expectedThirdSurveyId, collex2.getSurveyId());
    assertNotEquals(expectedSecondSurveyId, collex2.getSurveyId());
    assertNotEquals(expectedSurveyId, collex2.getSurveyId());
    removeStub(stubMapping);
  }

  private StubMapping createCollexStub(String surveyId) {
    return stubFor(
        get(urlPathMatching("/collectionexercises/.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"576357f9-697c-455a-a2c3-1b529752b245\",\"surveyId\":\""
                            + surveyId
                            + "\",\"name\":null,\"actualExecutionDateTime\":null,\"scheduledExecutionDateTime\":null,\"scheduledStartDateTime\":null,\"actualPublishDateTime\":null,\"periodStartDateTime\":null,\"periodEndDateTime\":null,\"scheduledReturnDateTime\":null,\"scheduledEndDateTime\":null,\"executedBy\":null,\"state\":\"CREATED\",\"exerciseRef\":\"510085\",\"userDescription\":\"January 2018\",\"created\":\"2019-02-13T10:13:19.530Z\",\"updated\":null,\"deleted\":null,\"validationErrors\":null,\"events\":[]}")
                    .withTransformers("response-template")));
  }
}

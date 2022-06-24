package uk.gov.ons.ctp.response.casesvc.domain;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:/application-test.yml")
@Sql(
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:/delete-test-data.sql")
public class CaseGroupRepositoryTestIT {

  @Autowired private CaseGroupRepository actionCase;
  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Test
  public void testNoDateInView() throws Exception {
    List<CaseAction> collectionExercises = actionCase.findByCollectionExerciseId(UUID.randomUUID());
    Assert.assertTrue(collectionExercises.isEmpty());
  }

  //  @Test
  //  public void testDateInView() throws Exception {
  //    Random rnd = new Random();
  //
  //    int randNumber = 10000 + rnd.nextInt(900000);
  //
  //    UUID surveyId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");
  //
  //    collectionExerciseSvcClient.createCollectionExercise(
  //            surveyId, Integer.toString(randNumber), "January 2018");
  //
  //    CollectionExerciseDTO collex =
  //        collectionExerciseSvcClient.getCollectionExercises(surveyId.toString()).get(0);
  //
  //    UUID collectionExerciseId = collex.getId();
  //    // Given
  //    caseCreator.postSampleUnit("LMS0002", "H", UUID.randomUUID(), collectionExerciseId);
  //    Thread.sleep(2000);
  //
  //    // When
  //    List<CaseAction> collectionExercises =
  //        actionCase.findByCollectionExerciseId(collectionExerciseId);
  //    // Then
  //    Assert.assertEquals(1, collectionExercises.size());
  //    Assert.assertEquals(collectionExerciseId,
  // collectionExercises.get(0).getCollectionExerciseId());
  //    UUID caseId = collectionExercises.get(0).getCaseId();
  //    boolean isActiveEnrolment = collectionExercises.get(0).isActiveEnrolment();
  //
  //    CaseAction caseIdObject = actionCase.findByCaseId(caseId);
  //    Assert.assertEquals(collectionExerciseId, caseIdObject.getCollectionExerciseId());
  //
  //    List<CaseAction> caseObjects =
  //        actionCase.findByCollectionExerciseIdAndActiveEnrolment(
  //            collectionExerciseId, isActiveEnrolment);
  //    Assert.assertEquals(1, caseObjects.size());
  //    Assert.assertEquals(caseId, caseObjects.get(0).getCaseId());
  //
  //    List<UUID> caseIds = new ArrayList<>();
  //    caseIds.add(UUID.randomUUID());
  //    caseIds.add(UUID.randomUUID());
  //    caseIds.add(caseId);
  //    List<CaseAction> casesObject = actionCase.findByCaseIdIn(caseIds);
  //    Assert.assertEquals(1, casesObject.size());
  //    Assert.assertEquals(collectionExerciseId, casesObject.get(0).getCollectionExerciseId());
  //  }
}

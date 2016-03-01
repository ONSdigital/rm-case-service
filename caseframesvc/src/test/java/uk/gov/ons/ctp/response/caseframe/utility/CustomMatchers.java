package uk.gov.ons.ctp.response.caseframe.utility;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Created by philippe.brossier on 2/25/16.
 */
public class CustomMatchers {

  public static void compareArrayContent(String responseString, String jsonPath, Object... expectedElements) {
    List<Object> expectedList = Arrays.asList(expectedElements);
    int expectedNumberOfElements = expectedList.size();

    Object responseObject = JsonPath.read(responseString, jsonPath);
    Assert.assertTrue(responseObject instanceof JSONArray);
    JSONArray responseArray = (JSONArray) responseObject;

    Assert.assertEquals(expectedNumberOfElements, responseArray.size());

    List<Object> responseList = new ArrayList<>();
    for (int i = 0; i < expectedNumberOfElements; i++) {
      responseList.add(responseArray.get(i));
    }
    Assert.assertThat(responseList, containsInAnyOrder(expectedList.toArray()));
  }

}

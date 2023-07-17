package gred.nucleus.autocrop;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeFalse;


class AutoCropTest {
	public static final String PATH_TO_INPUT  = "../test-images/input/";
	public static final String PATH_TO_OUTPUT = "../test-images/output/";
	// Make sure the output folder is empty before running the test otherwise the checker might use the wrong files
	
	
	@Test
	@Tag("functional")
	void test() throws Exception {
		//assumeFalse(AutocropTestRunner.getNumberOfImages(PATH_TO_INPUT) == 0);
		AutocropTestRunner.run(PATH_TO_INPUT);
	}
	
}

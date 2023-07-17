package gred.nucleus.segmentation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeFalse;


class SegmentationTest {
	public static final String PATH_TO_INPUT  = "test-images/input/";
	public static final String PATH_TO_OUTPUT = "test-images/output/";
	
	
	@Test
	@Tag("functional")
	void test() throws Exception {
		int nImages = SegmentationTestRunner.getNumberOfImages(PATH_TO_INPUT);
		System.out.println(nImages);
		assumeFalse(nImages == 0);
		SegmentationTestRunner.run(PATH_TO_INPUT);
	}
	
}
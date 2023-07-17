package gred.nucleus.autocrop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class is use to filter autocrop boxes intersecting in 2D. This process is a default option used in autocrop :
 * <p>
 * Parameter : boolean boxesRegrouping
 * <p>
 * Here we regroup boxes with a certain percentage of surface intersection : if SurfaceA intersect SurfaceB >50% &&
 * SurfaceB intersect SurfaceA >50%
 * <p> You can define the percent of surface intersection parameter in Autocrop parameters:
 * <p> Parameter : int boxesPercentSurfaceToFilter
 */


public class RectangleIntersection {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/** List of boxes Rectangle : xMin , yMin , width , height */
	List<Rectangle>       listRectangle      = new ArrayList<>();
	/** Slice coordinate associated to the rectangles(boxes) */
	List<String>          zSlices            = new ArrayList<>();
	/** List of rectangle intersected detected */
	List<String>          rectangleIntersect = new ArrayList<>();
	/** Number of intersections per rectangles */
	Map<Integer, Integer> countIntersect     = new HashMap<>();
	/** Final list of rectangles after re rectangle computations */
	List<String>          finalListRectangle = new ArrayList<>();
	/** Boolean to check if new rectangles are computed */
	boolean               newBoxesAdded      = false;
	/** Autocrop parameter */
	AutocropParameters    autocropParameters;
	
	
	/**
	 * Constructor getting list of boxes computed in autocrop class. Initialisation of a list of 2D rectangles and a
	 * list of Z stack associated (zMin-zMax).
	 *
	 * @param boxes              List of boxes
	 * @param autocropParameters Autocrop parameters
	 */
	public RectangleIntersection(Map<Double, Box> boxes, AutocropParameters autocropParameters) {
		this.autocropParameters = autocropParameters;
		for (Map.Entry<Double, Box> entry : new TreeMap<>(boxes).entrySet()) {
			Box box       = entry.getValue();
			int boxWidth  = box.getXMax() - box.getXMin();
			int boxHeight = box.getYMax() - box.getYMin();
			int boxSlice  = box.getZMax() - box.getZMin();
			
			this.listRectangle.add(new Rectangle(box.getXMin(), box.getYMin(), boxWidth, boxHeight));
			this.zSlices.add(box.getZMin() + "-" + boxSlice);
		}
	}
	
	
	/**
	 * Compute the percentage of surface intersecting between r1 and r2.
	 *
	 * @param r1 Rectangle 1
	 * @param r2 Rectangle 2
	 *
	 * @return percent of overlap of r1
	 */
	public static double percentOf2Rectangles(Rectangle2D r1, Rectangle2D r2) {
		Rectangle2D r = new Rectangle2D.Double();
		Rectangle2D.intersect(r1, r2, r);
		double fr1 = r1.getWidth() * r1.getHeight();                // area of "r1"
		double f   = r.getWidth() * r.getHeight();                  // area of "r" - overlap
		return (fr1 == 0 || f <= 0) ? 0 : (f / fr1) * 100;          // overlap percentage
	}
	
	
	/**
	 * Class to run the boxes merge process
	 * <p> Step 1 : detecting boxes intersections</li>
	 * <p> Step 2 : group rectangle intersecting</li>
	 * <p> Step 3 : compile new rectangle</li>
	 */
	public void runRectangleRecompilation() {
		this.newBoxesAdded = true;
		int tours = 0;
		while (this.newBoxesAdded) {
			tours++;
			computeIntersection();
			rectangleRegroup();
			recompileRectangle();
		}
	}
	
	
	/** Regroup list of rectangles intersecting */
	public void computeIntersection() {
		this.rectangleIntersect.clear();
		
		for (int i = 0; i < this.listRectangle.size(); i++) {
			for (int y = 0; y < this.listRectangle.size(); y++) {
				
				if (((i != y)) &&
				    (!((this.rectangleIntersect.contains(i + "-" + y)))) &&
				    (!((this.rectangleIntersect.contains(y + "-" + i))))) {
					
					if (listRectangle.get(i).intersects(this.listRectangle.get(y))) {
						
						if (percentOf2Rectangles(this.listRectangle.get(i), this.listRectangle.get(y)) >
						    autocropParameters.getBoxesPercentSurfaceToFilter() ||
						    percentOf2Rectangles(this.listRectangle.get(y), this.listRectangle.get(i)) >
						    autocropParameters.getBoxesPercentSurfaceToFilter()) {
							
							this.rectangleIntersect.add(i + "-" + y);
							this.rectangleIntersect.add(y + "-" + i);
							if (this.countIntersect.containsKey(i)) {
								this.countIntersect.put(i, this.countIntersect.get(i) + 1);
							} else {
								this.countIntersect.put(i, 1);
							}
						}
					}
				}
			}
		}
	}
	
	
	/** Regroup of rectangle intersecting recursively */
	public void rectangleRegroup() {
		this.finalListRectangle.clear();
		for (Map.Entry<Integer, Integer> entry : this.countIntersect.entrySet()) {
			StringBuilder listRectangleConnected          = new StringBuilder(String.valueOf(entry.getKey()));
			String        listRectangleConnectedStartTurn = String.valueOf(entry.getKey());
			for (int i = 0; i < this.rectangleIntersect.size(); i++) {
				
				String[] splitIntersect = this.rectangleIntersect.get(i).split("-");
				if (splitIntersect[0].equals(Integer.toString(entry.getKey()))) {
					String[] splitList = this.rectangleIntersect.get(i).split("-");
					listRectangleConnected.append("-").append(splitList[splitList.length - 1]);
					this.rectangleIntersect.remove(i);
					this.rectangleIntersect.remove(splitList[splitList.length - 1] + "-" + entry.getKey());
					List<String> listToIterateThrough = new ArrayList<>();
					listToIterateThrough.add(splitList[splitList.length - 1]);
					while (!listToIterateThrough.isEmpty()) {
						for (int y = 0; y < this.rectangleIntersect.size(); y++) {
							String[] splitIntersect2 = this.rectangleIntersect.get(y).split("-");
							if (splitIntersect2[0].equals(listToIterateThrough.get(0))) {
								String[] splitList2 = this.rectangleIntersect.get(y).split("-");
								listToIterateThrough.add(splitList2[splitList.length - 1]);
								listRectangleConnected.append("-").append(splitList2[splitList.length - 1]);
								String[] splitCurrentRectangleConnected = listRectangleConnected.toString().split("-");
								this.rectangleIntersect.remove(y);
								this.rectangleIntersect.remove(splitList2[splitList.length - 1] +
								                               "-" +
								                               listToIterateThrough.get(0));
								for (String s : splitCurrentRectangleConnected) {
									this.rectangleIntersect.remove(splitList2[splitList.length - 1] + "-" + s);
									this.rectangleIntersect.remove(s + "-" + splitList2[splitList.length - 1]);
								}
								y = 0;
							}
						}
						listToIterateThrough.remove(0);
					}
				}
				if (!(listRectangleConnected.toString().equals(listRectangleConnectedStartTurn))) {
					i--;
					listRectangleConnectedStartTurn = listRectangleConnected.toString();
				}
			}
			this.finalListRectangle.add(listRectangleConnected.toString());
		}
	}
	
	
	/** Compile of new rectangle by getting extreme coordinate of a group of rectangles. */
	public void recompileRectangle() {
		this.newBoxesAdded = false;
		List<Rectangle> listOfRectangleToAdd       = new ArrayList<>();
		List<String>    listOfRectangleZSliceToAdd = new ArrayList<>();
		List<Rectangle> listOfRectangleToRemove    = new ArrayList<>();
		for (String value : this.finalListRectangle) {
			String[] splitList2       = value.split("-");
			double   xMixNewRectangle = 0;
			double   yMinNewRectangle = 0;
			double   maxWidth         = 0;
			double   maxHeight        = 0;
			int      minZSlice        = 0;
			int      maxZSlice        = 0;
			if (splitList2.length > 1) {
				for (String s : splitList2) {
					int tmp = Integer.parseInt(s);
					if ((this.listRectangle.get(tmp).getX() < xMixNewRectangle) || (xMixNewRectangle == 0)) {
						xMixNewRectangle = this.listRectangle.get(tmp).getX();
					}
					if ((this.listRectangle.get(tmp).getY() < yMinNewRectangle) || (yMinNewRectangle == 0)) {
						yMinNewRectangle = this.listRectangle.get(tmp).getY();
					}
					if (((this.listRectangle.get(tmp).getX() + this.listRectangle.get(tmp).getWidth()) > maxWidth) ||
					    (maxWidth == 0)) {
						maxWidth = this.listRectangle.get(tmp).getX() + this.listRectangle.get(tmp).getWidth();
					}
					if (((this.listRectangle.get(tmp).getY() + this.listRectangle.get(tmp).getHeight()) > maxHeight) ||
					    (maxHeight == 0)) {
						maxHeight = this.listRectangle.get(tmp).getY() + this.listRectangle.get(tmp).getHeight();
					}
					
					String[] zSliceTMP = this.zSlices.get(tmp).split("-");
					if ((Integer.parseInt(zSliceTMP[0]) < minZSlice) || (minZSlice == 0)) {
						minZSlice = Integer.parseInt(zSliceTMP[0]);
					}
					if (((Integer.parseInt(zSliceTMP[0] + Integer.valueOf(zSliceTMP[1])) > maxZSlice) ||
					     (maxZSlice == 0))) {
						maxZSlice = Integer.parseInt(zSliceTMP[0]) + Integer.parseInt(zSliceTMP[1]);
					}
					listOfRectangleToRemove.add(new Rectangle((int) this.listRectangle.get(tmp).getX(),
					                                          (int) this.listRectangle.get(tmp).getY(),
					                                          (int) this.listRectangle.get(tmp).getWidth(),
					                                          (int) this.listRectangle.get(tmp).getHeight()));
				}
				
				maxZSlice -= minZSlice;
				listOfRectangleZSliceToAdd.add(minZSlice + "-" + maxZSlice);
				maxWidth = (int) maxWidth - (int) xMixNewRectangle;
				maxHeight = (int) maxHeight - (int) yMinNewRectangle;
				listOfRectangleToAdd.add(new Rectangle((int) xMixNewRectangle,
				                                       (int) yMinNewRectangle,
				                                       (int) maxWidth,
				                                       (int) maxHeight));
			}
		}
		LOGGER.debug("{} boxes will be merged in {} new boxes",
		             listOfRectangleToRemove.size(),
		             listOfRectangleToAdd.size());
		for (int i = 0; i < listOfRectangleToAdd.size(); i++) {
			this.newBoxesAdded = true;
			listRectangle.add(listOfRectangleToAdd.get(i));
			zSlices.add(listOfRectangleZSliceToAdd.get(i));
		}
		for (Rectangle rectangle : listOfRectangleToRemove) {
			this.newBoxesAdded = true;
			int indexRectangleRemove = listRectangle.indexOf(rectangle);
			if(indexRectangleRemove!= -1) {
				listRectangle.remove(indexRectangleRemove);
				zSlices.remove(indexRectangleRemove);
			}
		}
	}
	
	
	/**
	 * Computing the list of the new rectangles using boxes coordinates format.
	 *
	 * @return list of new boxes
	 */
	public Map<Double, Box> getNewBoxes() {
		Map<Double, Box> boxes = new HashMap<>();
		
		
		for (int i = 0; i < listRectangle.size(); i++) {
			String[] zSliceTMP = this.zSlices.get(i).split("-");
			short    tmpXMax   = (short) (this.listRectangle.get(i).getX() + this.listRectangle.get(i).getWidth());
			short    tmpYMax   = (short) (this.listRectangle.get(i).getY() + this.listRectangle.get(i).getHeight());
			short    tmpZMax   = (short) (Short.parseShort(zSliceTMP[0]) + Short.parseShort(zSliceTMP[1]));
			if (tmpZMax == 0) {
				tmpZMax = 1;
			}
			Box box = new Box((short) (this.listRectangle.get(i).getX()),
			                  tmpXMax,
			                  (short) (this.listRectangle.get(i).getY()),
			                  tmpYMax,
			                  Short.parseShort(zSliceTMP[0]),
			                  tmpZMax);
			boxes.put((double) i, box);
		}
		return boxes;
	}
	
}

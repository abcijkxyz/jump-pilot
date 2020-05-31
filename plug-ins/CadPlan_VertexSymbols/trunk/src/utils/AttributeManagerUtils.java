package com.cadplan.jump.utils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerEventType;

public class AttributeManagerUtils {

	//==========================================================================
	// These two methods (modified)  have been taken from ViewSchemaPlugin to modify an
	// existing schema
	//========================================================================
	public void addAttribute(final Layer layer, String newAttName, AttributeType newAttType)
	{
		FeatureSchema newSchema = new FeatureSchema();
		FeatureSchema oldSchema = layer.getFeatureCollectionWrapper().getFeatureSchema();
		int numAtt = oldSchema.getAttributeCount();
		for (int i=0; i < numAtt; i++)
		{
			String attName = oldSchema.getAttributeName(i);
			AttributeType attType = oldSchema.getAttributeType(i);
			newSchema.addAttribute(attName, attType);
		}
		newSchema.addAttribute(newAttName,newAttType);
		List<Feature> originalFeatures = layer.getFeatureCollectionWrapper().getFeatures();
		ArrayList<Feature> tempFeatures = new ArrayList<Feature>();
		//Two-phase commit. Phase 1: check that no conversion errors occur. [Jon Aquino]
		for (Iterator<?> i = layer.getFeatureCollectionWrapper().iterator();i.hasNext();)
		{
			Feature feature = (Feature) i.next();
			tempFeatures.add(convert(feature, newSchema));
		}
		//Phase 2: commit. [Jon Aquino]
		for (int i = 0; i < originalFeatures.size(); i++)
		{
			Feature originalFeature = originalFeatures.get(i);
			Feature tempFeature = tempFeatures.get(i);
			//Modify existing features rather than creating new features, because
			//there may be references to the existing features (e.g. Attribute Viewers).
			//[Jon Aquino]
			originalFeature.setSchema(tempFeature.getSchema());
			originalFeature.setAttributes(tempFeature.getAttributes());
		}
		//Non-undoable. [Jon Aquino]
		layer.getLayerManager().getUndoableEditReceiver().getUndoManager()
		.discardAllEdits();
		layer.setFeatureCollection(new FeatureDataset(originalFeatures,
				newSchema));
		layer.fireLayerChanged(LayerEventType.METADATA_CHANGED);
	}

	public  void delAttribute( final Layer layer,  String delAttName)
	{
		FeatureSchema newSchema = new FeatureSchema();
		FeatureSchema oldSchema = layer.getFeatureCollectionWrapper().getFeatureSchema();
		int numAtt = oldSchema.getAttributeCount();
		int delItem = -1;
		for (int i=0; i < numAtt; i++)
		{
			String attName = oldSchema.getAttributeName(i);
			if(!attName.equals(delAttName))
			{
				AttributeType attType = oldSchema.getAttributeType(i);
				newSchema.addAttribute(attName, attType);
			}
			else
			{
				delItem = i;
			}
		}

		List<Feature> originalFeatures = layer.getFeatureCollectionWrapper().getFeatures();
		ArrayList<Feature> tempFeatures = new ArrayList<Feature>();

		//Two-phase commit. Phase 1: check that no conversion errors occur. [Jon Aquino]
		for (Iterator<?> i = layer.getFeatureCollectionWrapper().iterator();i.hasNext();)
		{
			Feature feature = (Feature) i.next();
			tempFeatures.add(convertDel(feature, delItem, newSchema));
		}
		//Phase 2: commit. [Jon Aquino]
		for (int i = 0; i < originalFeatures.size(); i++)
		{
			Feature originalFeature = originalFeatures.get(i);
			Feature tempFeature = tempFeatures.get(i);

			//Modify existing features rather than creating new features, because
			//there may be references to the existing features (e.g. Attribute Viewers).
			//[Jon Aquino]
			originalFeature.setSchema(tempFeature.getSchema());
			originalFeature.setAttributes(tempFeature.getAttributes());
		}
		//Non-undoable. [Jon Aquino]
		layer.getLayerManager().getUndoableEditReceiver().getUndoManager()
		.discardAllEdits();
		layer.setFeatureCollection(new FeatureDataset(originalFeatures,
				newSchema));
		layer.fireLayerChanged(LayerEventType.METADATA_CHANGED);
	}

	private static Feature convert(Feature oldFeature, FeatureSchema newSchema)
	{
		Feature newFeature = new BasicFeature(newSchema);
		Object [] oldAttributes= oldFeature.getAttributes();
		for (int i = 0; i < oldAttributes.length; i++)
		{
			newFeature.setAttribute(i, oldAttributes[i]);
		}
		newFeature.setAttribute(oldAttributes.length,0);
		return newFeature;
	}

	private static Feature convertDel(Feature oldFeature, int delItem, FeatureSchema newSchema)
	{
		Feature newFeature = new BasicFeature(newSchema);
		Object [] oldAttributes= oldFeature.getAttributes();
		int j = 0;
		for (int i = 0; i < oldAttributes.length; i++)
		{
			if(i != delItem)
			{
				newFeature.setAttribute(j, oldAttributes[i]);
				j++;
			}
		}



		return newFeature;
	}

}

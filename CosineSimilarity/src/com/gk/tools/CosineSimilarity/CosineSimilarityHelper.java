package com.gk.tools.CosineSimilarity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CosineSimilarityHelper
{
public static Double calculateCosineSimilarity(HashMap<String, Double> firstFeatures, HashMap<String, Double> secondFeatures)
{
Double similarity = Double.valueOf(0.0D);
Double sum = Double.valueOf(0.0D);
Double fnorm = Double.valueOf(0.0D);
Double snorm = Double.valueOf(0.0D);
Set<String> fkeys = firstFeatures.keySet();
Iterator<String> fit = fkeys.iterator();
while (fit.hasNext())
{
String featurename = (String)fit.next();
boolean containKey = secondFeatures.containsKey(featurename);
if (containKey) {
sum = Double.valueOf(sum.doubleValue() + ((Double)firstFeatures.get(featurename)).doubleValue() * ((Double)secondFeatures.get(featurename)).doubleValue());
}
}
fnorm = calculateNorm(firstFeatures);
snorm = calculateNorm(secondFeatures);
similarity = Double.valueOf(sum.doubleValue() / (fnorm.doubleValue() * snorm.doubleValue()));
return similarity;
}

public static Double calculateNorm(HashMap<String, Double> feature)
{
Double norm = Double.valueOf(0.0D);
Set<String> keys = feature.keySet();
Iterator<String> it = keys.iterator();
while (it.hasNext())
{
String featurename = (String)it.next();
norm = Double.valueOf(norm.doubleValue() + Math.pow(((Double)feature.get(featurename)).doubleValue(), 2.0D));
}
return Double.valueOf(Math.sqrt(norm.doubleValue()));
}
}
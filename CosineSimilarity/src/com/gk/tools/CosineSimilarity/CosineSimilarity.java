package com.gk.tools.CosineSimilarity;

import com.github.pmerienne.trident.ml.nlp.TFIDF;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.Node;

public class CosineSimilarity
{
public List<List<String>> docs;
private TFIDF tf_idf;

public CosineSimilarity()
{
this.docs = new ArrayList();
}

public void addDocToCorpus(List<String> list)
{
this.docs.add(list);
}

public void calculate_tf_idf()
{
this.tf_idf = new TFIDF(this.docs, 100000);
}

public Double calculate_cosinus_similarity(List<String> first_fea, List<String> second_fea)
{
if (this.tf_idf == null) {
calculate_tf_idf();
}
double[] fea1 = this.tf_idf.extractFeatures(first_fea);
double[] fea2 = this.tf_idf.extractFeatures(second_fea);


Double d = CosineSimilarityHelper.calculateCosineSimilarity(getHash(first_fea, fea1), getHash(second_fea, fea2));

return d;
}

private void printExtract(double[] fea)
{
int i = 0;
for (String term : this.tf_idf.getTermsInverseDocumentFrequencies().keySet())
{
System.out.print(term + " " + fea[i] + "\n");
i++;
}
}

private HashMap<String, Double> getHash(List<String> words, double[] fea)
{
HashMap<String, Double> hash_fea = new HashMap();
int i = 0;
for (String term : this.tf_idf.getTermsInverseDocumentFrequencies().keySet())
{
hash_fea.put(term, Double.valueOf(fea[i]));
i++;
}
return hash_fea;
}

public Node getBestRelevantNodeFrom(String textContent, Node[] nodes1, Node[] nodes2)
{
addDocToCorpus(getToken(textContent));
for (Node n : nodes1) {
addDocToCorpus(getToken(n.getTextContent()));
}
for (Node n : nodes2) {
addDocToCorpus(getToken(n.getTextContent()));
}
double bestResult = 0.0D;
Node bestRelevantNode = null;
for (Node n : nodes1)
{
double d = calculate_cosinus_similarity(
getToken(textContent),
getToken(n.getTextContent())).doubleValue();
if (d > bestResult)
{
bestResult = d;
bestRelevantNode = n;
}
}
for (Node n : nodes2)
{
double d = calculate_cosinus_similarity(
getToken(textContent),
getToken(n.getTextContent())).doubleValue();
if (d > bestResult)
{
bestResult = d;
bestRelevantNode = n;
}
}
return bestRelevantNode;
}

public static void main(String[] args)
throws Exception
{
String content = "Accompagné d’une importante délégation des Nations unies ainsi que de son épouse, le secrétaire général de l’Organisation des Nations unies (ONU), Ban Ki-moon, a entamé ce mercredi sa toute première visite officielle au Costa Rica, où il a été reçu à l’aéroport de Juan Santamaría par le président costaricain, Luis Guillermo Solís, et son ministre des Affaires étrangères, Manuel González. Les deux hommes se sont entretenus sur quelques sujets d’actualité, dont le conflit israélo-palestinien, la problématique globale des droits de la personne, de même que sur les changements climatiques. Le président Solis a par ailleurs tenu à rassurer son hôte que son pays s’investirait, le moment venu, dans sa réélection au poste de secrétaire général de l’ONU. La dernière visite officielle d’un secrétaire général de l’ONU au Costa Rica remonte au mois de mars 2002.";
HashMap<String, String> Map = new HashMap();

Map.put("CONTENT", content);
Map.put("H1", "L'actualité des Amériques");
Map.put("H2", "Abonnez-vous");
Map.put("H7", "Zone audio-vidéo");
Map.put("H8", "Calendrier");
Map.put("H9", "Suivez-nous");
Map.put("H4", "Rubriques");
Map.put("H5", "Visite historique du secrétaire général de l'ONU au Costa Rica");
Map.put("H6", "C'est un putain de test secrétaire l'ONU");

CosineSimilarity test = new CosineSimilarity();
test.addDocToCorpus(getToken((String)Map.get("CONTENT")));
test.addDocToCorpus(getToken((String)Map.get("H1")));
test.addDocToCorpus(getToken((String)Map.get("H2")));
test.addDocToCorpus(getToken((String)Map.get("H7")));
test.addDocToCorpus(getToken((String)Map.get("H8")));
test.addDocToCorpus(getToken((String)Map.get("H9")));
test.addDocToCorpus(getToken((String)Map.get("H4")));
test.addDocToCorpus(getToken((String)Map.get("H5")));
test.addDocToCorpus(getToken((String)Map.get("H6")));
for (Iterator<Map.Entry<String, String>> iterator = Map.entrySet().iterator(); iterator.hasNext();)
{
Map.Entry<String, String> type = (Map.Entry)iterator.next();
double d = test.calculate_cosinus_similarity(getToken((String)Map.get("CONTENT")), getToken((String)Map.get(type.getKey()))).doubleValue();
System.out.print((String)type.getKey() + " => " + d + "\n");
}
System.out.print("end\n");
}

public static List<String> getToken(String text)
{
String[] splitted = text.split(" ");
return new ArrayList(Arrays.asList(splitted));
}
}
package com.gk.apache.nutch.indexer.elastic;
import java.util.Map;

import org.apache.hadoop.util.ToolRunner;
import org.apache.nutch.indexer.IndexerJob;
import org.apache.nutch.indexer.NutchIndexWriterFactory;
import org.apache.nutch.metadata.Nutch;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Indexer for elasticsearch. Uses bulk operations with flushing in the background,
 * keeping track of elasticsearch responses by checking after every flush. When a 
 * previous flush has not finished yet before the next bulk is full, it will wait for it.
 * This mechanism will keep the servers from overloading.
 */
public class ElasticIndexerJob extends IndexerJob {

  public static Logger LOG = LoggerFactory.getLogger(ElasticIndexerJob.class);

  @Override
  public Map<String,Object> run(Map<String,Object> args) throws Exception {
    LOG.info("Starting");
    
    NutchIndexWriterFactory.addClassToConf(getConf(), ElasticWriter.class);
    String batchId = (String)args.get(Nutch.ARG_BATCH);    
    String clusterName = (String)args.get(ElasticConstants.CLUSTER);
    
    getConf().set(ElasticConstants.CLUSTER, clusterName);
    
    currentJob = createIndexJob(getConf(), "elastic-index [" + clusterName + "]", batchId);
    
    currentJob.waitForCompletion(true);
    ToolUtil.recordJobStatus(null, currentJob, results);
    
    LOG.info("Done");
    return results;
  }

  public void indexElastic(String clusterName, String batchId) throws Exception {
    run(ToolUtil.toArgMap(ElasticConstants.CLUSTER, clusterName,
                          Nutch.ARG_BATCH, batchId));
  }

  public int run(String[] args) throws Exception {
    if (args.length == 2) {
      //ok
    } else if (args.length == 4 && "-crawlId".equals(args[2])) {
      getConf().set(Nutch.CRAWL_ID_KEY, args[3]);
    } else {
      System.err.println("Usage: <elastic cluster name> (<batchId> | -all | -reindex) [-crawlId <id>]");
      return -1;      
    }
    indexElastic(args[0], args[1]);
    return 0;
  }

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(NutchConfiguration.create(), new ElasticIndexerJob(), args);
    System.exit(res);
  }
}

package org.ayakaji.msg;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Verifier {

	private static Logger LOGGER = Logger.getLogger(Verifier.class.getName());

	private static String jstr1 = "{\r\n" + "	\"sender\": \"10086911\",\r\n" + "	\"state\": \"OPEN\",\r\n"
			+ "	\"problemId\": \"503\",\r\n" + "	\"problemTitle\": \"Failure rate increase\",\r\n"
			+ "	\"problemDetails\": {\r\n" + "		\"id\": \"6092767276548770503_1604654100000V2\",\r\n"
			+ "		\"startTime\": 1604654100000,\r\n" + "		\"endTime\": -1,\r\n"
			+ "		\"displayName\": \"503\",\r\n" + "		\"impactLevel\": \"SERVICE\",\r\n"
			+ "		\"status\": \"OPEN\",\r\n" + "		\"severityLevel\": \"ERROR\",\r\n"
			+ "		\"commentCount\": 0,\r\n" + "		\"tagsOfAffectedEntities\": [{\r\n"
			+ "			\"context\": \"KUBERNETES\",\r\n" + "			\"key\": \"pod_id\",\r\n"
			+ "			\"value\": \"c0c3004c175516f932ea2e\"\r\n" + "		}, {\r\n"
			+ "			\"context\": \"KUBERNETES\",\r\n" + "			\"key\": \"appcharge\",\r\n"
			+ "			\"value\": \"charge\"\r\n" + "		}, {\r\n" + "			\"context\": \"KUBERNETES\",\r\n"
			+ "			\"key\": \"pod-template-hash\",\r\n" + "			\"value\": \"7bc967c9f6\"\r\n"
			+ "		}],\r\n" + "		\"rankedEvents\": [{\r\n" + "			\"startTime\": 1604654100000,\r\n"
			+ "			\"endTime\": -1,\r\n" + "			\"entityId\": \"SERVICE-CA81379F8610E1E1\",\r\n"
			+ "			\"entityName\": \"charge-* (/charge)\",\r\n" + "			\"severityLevel\": \"ERROR\",\r\n"
			+ "			\"impactLevel\": \"SERVICE\",\r\n" + "			\"eventType\": \"FAILURE_RATE_INCREASED\",\r\n"
			+ "			\"status\": \"OPEN\",\r\n" + "			\"severities\": [{\r\n"
			+ "				\"context\": \"FAILURE_RATE\",\r\n" + "				\"value\": 0.00426780479028821,\r\n"
			+ "				\"unit\": \"Ratio\"\r\n" + "			}],\r\n" + "			\"isRootCause\": false,\r\n"
			+ "			\"service\": \"charge-* (/charge)\",\r\n"
			+ "			\"serviceMethodGroup\": \"Dynamic web requests\",\r\n"
			+ "			\"affectedRequestsPerMinute\": 749.8\r\n" + "		}],\r\n"
			+ "		\"rankedImpacts\": [{\r\n" + "			\"entityId\": \"SERVICE-CA81379F8610E1E1\",\r\n"
			+ "			\"entityName\": \"charge-* (/charge)\",\r\n" + "			\"severityLevel\": \"ERROR\",\r\n"
			+ "			\"impactLevel\": \"SERVICE\",\r\n" + "			\"eventType\": \"FAILURE_RATE_INCREASED\"\r\n"
			+ "		}],\r\n" + "		\"affectedCounts\": {\r\n" + "			\"INFRASTRUCTURE\": 0,\r\n"
			+ "			\"SERVICE\": 1,\r\n" + "			\"APPLICATION\": 0,\r\n"
			+ "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n" + "		\"recoveredCounts\": {\r\n"
			+ "			\"INFRASTRUCTURE\": 0,\r\n" + "			\"SERVICE\": 0,\r\n"
			+ "			\"APPLICATION\": 0,\r\n" + "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n"
			+ "		\"hasRootCause\": false\r\n" + "	},\r\n" + "	\"receivers\": \"15069071152\"\r\n" + "}";

	private static String jstr2 = "{\r\n" + "	\"sender\": \"10086911\",\r\n" + "	\"state\": \"OPEN\",\r\n"
			+ "	\"problemId\": \"756\",\r\n" + "	\"problemTitle\": \"Long garbage-collection time\",\r\n"
			+ "	\"problemDetails\": {\r\n" + "		\"id\": \"-3160937899276123756_1604681640000V2\",\r\n"
			+ "		\"startTime\": 1604681640000,\r\n" + "		\"endTime\": -1,\r\n"
			+ "		\"displayName\": \"756\",\r\n" + "		\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "		\"status\": \"OPEN\",\r\n" + "		\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "		\"commentCount\": 0,\r\n" + "		\"tagsOfAffectedEntities\": [],\r\n"
			+ "		\"rankedEvents\": [{\r\n" + "			\"startTime\": 1604681640000,\r\n"
			+ "			\"endTime\": -1,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-D8BFC697C593CEEE\",\r\n"
			+ "			\"entityName\": \"Apache Spark 134.80.160.234 (CoarseGrainedExecutorBackend)\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"OPEN\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": false\r\n" + "		}],\r\n"
			+ "		\"rankedImpacts\": [{\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-D8BFC697C593CEEE\",\r\n"
			+ "			\"entityName\": \"Apache Spark 134.80.160.234 (CoarseGrainedExecutorBackend)\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}],\r\n" + "		\"affectedCounts\": {\r\n" + "			\"INFRASTRUCTURE\": 1,\r\n"
			+ "			\"SERVICE\": 0,\r\n" + "			\"APPLICATION\": 0,\r\n"
			+ "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n" + "		\"recoveredCounts\": {\r\n"
			+ "			\"INFRASTRUCTURE\": 0,\r\n" + "			\"SERVICE\": 0,\r\n"
			+ "			\"APPLICATION\": 0,\r\n" + "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n"
			+ "		\"hasRootCause\": false\r\n" + "	},\r\n" + "	\"receivers\": \"15069071152\"\r\n" + "}";

	private static String jstr3 = "{\r\n" + "	\"sender\": \"10086911\",\r\n" + "	\"state\": \"RESOLVED\",\r\n"
			+ "	\"problemId\": \"956\",\r\n" + "	\"problemTitle\": \"2 infrastructure problems\",\r\n"
			+ "	\"problemDetails\": {\r\n" + "		\"id\": \"-7391644284447974956_1604681460000V2\",\r\n"
			+ "		\"startTime\": 1604681460000,\r\n" + "		\"endTime\": 1604683200000,\r\n"
			+ "		\"displayName\": \"956\",\r\n" + "		\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "		\"status\": \"CLOSED\",\r\n" + "		\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "		\"commentCount\": 0,\r\n" + "		\"tagsOfAffectedEntities\": [],\r\n"
			+ "		\"rankedEvents\": [{\r\n" + "			\"startTime\": 1604681520000,\r\n"
			+ "			\"endTime\": 1604682360000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B5404DA51D12C35B\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682120000,\r\n" + "			\"endTime\": 1604682660000,\r\n"
			+ "			\"entityId\": \"HOST-0113D7502F446AA7\",\r\n"
			+ "			\"entityName\": \"c6h155.bdx.sd.cmcc\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"SLOW_DISK\",\r\n"
			+ "			\"resourceId\": \"DISK-AD5E948ECF0C5F1B\",\r\n" + "			\"resourceName\": \"/data3\",\r\n"
			+ "			\"status\": \"CLOSED\",\r\n" + "			\"severities\": [],\r\n"
			+ "			\"isRootCause\": true\r\n" + "		}, {\r\n" + "			\"startTime\": 1604682300000,\r\n"
			+ "			\"endTime\": 1604683140000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B76D1A19816C85CE\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681700000,\r\n" + "			\"endTime\": 1604682300000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-EDC3C56BCAAA06A2\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683440000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-82D6E08ABF944720\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681700000,\r\n" + "			\"endTime\": 1604682300000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-142364EA8F53BA3E\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681700000,\r\n" + "			\"endTime\": 1604682300000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-593F9DCF3D086203\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681460000,\r\n" + "			\"endTime\": 1604681940000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-C1A95BFD8B06FBEC\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681880000,\r\n" + "			\"endTime\": 1604682480000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-76918D3E84890285\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681760000,\r\n" + "			\"endTime\": 1604682480000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-D03525BE00F0C5B4\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681700000,\r\n" + "			\"endTime\": 1604682360000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-8111CB5D8E719A5D\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604681700000,\r\n" + "			\"endTime\": 1604682300000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-ADD611D96C654302\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683140000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-878514F07A17DE1B\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683260000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-C2765C856BE30AA4\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683140000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-3FD5B20A73C4F4BA\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683380000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-7603015C63D81131\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}, {\r\n"
			+ "			\"startTime\": 1604682360000,\r\n" + "			\"endTime\": 1604683140000,\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B14DF80E555297F5\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n"
			+ "			\"eventType\": \"HIGH_GC_ACTIVITY\",\r\n" + "			\"status\": \"CLOSED\",\r\n"
			+ "			\"severities\": [],\r\n" + "			\"isRootCause\": true\r\n" + "		}],\r\n"
			+ "		\"rankedImpacts\": [{\r\n"
			+ "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B5404DA51D12C35B\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"HOST-0113D7502F446AA7\",\r\n"
			+ "			\"entityName\": \"c6h155.bdx.sd.cmcc\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"SLOW_DISK\",\r\n"
			+ "			\"resourceId\": \"DISK-AD5E948ECF0C5F1B\",\r\n" + "			\"resourceName\": \"/data3\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B76D1A19816C85CE\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-EDC3C56BCAAA06A2\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-82D6E08ABF944720\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-142364EA8F53BA3E\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-593F9DCF3D086203\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-C1A95BFD8B06FBEC\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-76918D3E84890285\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-D03525BE00F0C5B4\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-8111CB5D8E719A5D\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-ADD611D96C654302\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-878514F07A17DE1B\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-C2765C856BE30AA4\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-3FD5B20A73C4F4BA\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-7603015C63D81131\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}, {\r\n" + "			\"entityId\": \"PROCESS_GROUP_INSTANCE-B14DF80E555297F5\",\r\n"
			+ "			\"entityName\": \"org.apache.tez.runtime.task.TezChild\",\r\n"
			+ "			\"severityLevel\": \"RESOURCE_CONTENTION\",\r\n"
			+ "			\"impactLevel\": \"INFRASTRUCTURE\",\r\n" + "			\"eventType\": \"HIGH_GC_ACTIVITY\"\r\n"
			+ "		}],\r\n" + "		\"affectedCounts\": {\r\n" + "			\"INFRASTRUCTURE\": 0,\r\n"
			+ "			\"SERVICE\": 0,\r\n" + "			\"APPLICATION\": 0,\r\n"
			+ "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n" + "		\"recoveredCounts\": {\r\n"
			+ "			\"INFRASTRUCTURE\": 17,\r\n" + "			\"SERVICE\": 0,\r\n"
			+ "			\"APPLICATION\": 0,\r\n" + "			\"ENVIRONMENT\": 0\r\n" + "		},\r\n"
			+ "		\"hasRootCause\": true\r\n" + "	},\r\n" + "	\"receivers\": \"15069071152\"\r\n" + "}";

	private static String jstr4 = "{\r\n" + "\"sender\":\"10086911\",\r\n" + "\"state\":\"OPEN\",\r\n"
			+ "\"problemId\":\"947\",\r\n" + "\"problemTitle\":\"Long garbage-collection time\",\r\n"
			+ "\"problemDetails\":{\"id\":\"-8541819226852317947_1604681760000V2\",\"startTime\":1604681760000,\"endTime\":-1,\"displayName\":\"947\",\"impactLevel\":\"INFRASTRUCTURE\",\"status\":\"OPEN\",\"severityLevel\":\"RESOURCE_CONTENTION\",\"commentCount\":0,\"tagsOfAffectedEntities\":[],\"rankedEvents\":[{\"startTime\":1604681760000,\"endTime\":-1,\"entityId\":\"PROCESS_GROUP_INSTANCE-00FCF637CD9E0B0A\",\"entityName\":\"Apache Spark 134.80.160.234 (CoarseGrainedExecutorBackend)\",\"severityLevel\":\"RESOURCE_CONTENTION\",\"impactLevel\":\"INFRASTRUCTURE\",\"eventType\":\"HIGH_GC_ACTIVITY\",\"status\":\"OPEN\",\"severities\":[],\"isRootCause\":false}],\"rankedImpacts\":[{\"entityId\":\"PROCESS_GROUP_INSTANCE-00FCF637CD9E0B0A\",\"entityName\":\"Apache Spark 134.80.160.234 (CoarseGrainedExecutorBackend)\",\"severityLevel\":\"RESOURCE_CONTENTION\",\"impactLevel\":\"INFRASTRUCTURE\",\"eventType\":\"HIGH_GC_ACTIVITY\"}],\"affectedCounts\":{\"INFRASTRUCTURE\":1,\"SERVICE\":0,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"recoveredCounts\":{\"INFRASTRUCTURE\":0,\"SERVICE\":0,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"hasRootCause\":false},\r\n"
			+ "\"receivers\":\"15069071152\"\r\n" + "}";

	private static String jstr5 = "{\r\n" + "\"sender\":\"10086911\",\r\n" + "\"state\":\"RESOLVED\",\r\n"
			+ "\"problemId\":\"361\",\r\n" + "\"problemTitle\":\"Connectivity problem\",\r\n"
			+ "\"problemDetails\":{\"id\":\"-8231804528962734361_1604681880000V2\",\"startTime\":1604681880000,\"endTime\":1604683260000,\"displayName\":\"361\",\"impactLevel\":\"INFRASTRUCTURE\",\"status\":\"CLOSED\",\"severityLevel\":\"ERROR\",\"commentCount\":0,\"tagsOfAffectedEntities\":[],\"rankedEvents\":[{\"startTime\":1604681880000,\"endTime\":1604683560000,\"entityId\":\"PROCESS_GROUP_INSTANCE-42B463659D1A9A99\",\"entityName\":\"org.apache.flink.yarn.entrypoint.YarnJobClusterEntrypoint\",\"severityLevel\":\"ERROR\",\"impactLevel\":\"INFRASTRUCTURE\",\"eventType\":\"HIGH_CONNECTIVITY_FAILURES\",\"status\":\"CLOSED\",\"severities\":[],\"isRootCause\":true}],\"rankedImpacts\":[{\"entityId\":\"PROCESS_GROUP_INSTANCE-42B463659D1A9A99\",\"entityName\":\"org.apache.flink.yarn.entrypoint.YarnJobClusterEntrypoint\",\"severityLevel\":\"ERROR\",\"impactLevel\":\"INFRASTRUCTURE\",\"eventType\":\"HIGH_CONNECTIVITY_FAILURES\"}],\"affectedCounts\":{\"INFRASTRUCTURE\":0,\"SERVICE\":0,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"recoveredCounts\":{\"INFRASTRUCTURE\":1,\"SERVICE\":0,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"hasRootCause\":true},\r\n"
			+ "\"receivers\":\"15069071152\"\r\n" + "}";

	private static String jstr6 = "{\r\n" + 
			"\"sender\":\"10086911\",\r\n" + 
			"\"state\":\"OPEN\",\r\n" + 
			"\"problemId\":\"19\",\r\n" + 
			"\"problemTitle\":\"Failure rate increase\",\r\n" + 
			"\"problemDetails\":{\"id\":\"-8847639515508209019_1604908440000V2\",\"startTime\":1604908440000,\"endTime\":-1,\"displayName\":\"19\",\"impactLevel\":\"SERVICE\",\"status\":\"OPEN\",\"severityLevel\":\"ERROR\",\"commentCount\":0,\"tagsOfAffectedEntities\":[{\"context\":\"KUBERNETES\",\"key\":\"pod-template-hash\",\"value\":\"576cc687b6\"},{\"context\":\"KUBERNETES\",\"key\":\"pod_id\",\"value\":\"c0c3004c175516f932fa52\"},{\"context\":\"KUBERNETES\",\"key\":\"appuicustsvc\",\"value\":\"uicustsvc\"}],\"rankedEvents\":[{\"startTime\":1604908440000,\"endTime\":-1,\"entityId\":\"SERVICE-585CC7171418D6A7\",\"entityName\":\"crm3 (/ui-custsvc)\",\"severityLevel\":\"ERROR\",\"impactLevel\":\"SERVICE\",\"eventType\":\"FAILURE_RATE_INCREASED\",\"status\":\"OPEN\",\"severities\":[{\"context\":\"FAILURE_RATE\",\"value\":0.10080122947692871,\"unit\":\"Ratio\"}],\"isRootCause\":true,\"service\":\"crm3 (/ui-custsvc)\",\"serviceMethod\":\"/ui-custsvc/u-route/custsvc/chgprod/prodRecCheck\",\"affectedRequestsPerMinute\":216.06087}],\"rankedImpacts\":[{\"entityId\":\"SERVICE-585CC7171418D6A7\",\"entityName\":\"crm3 (/ui-custsvc)\",\"severityLevel\":\"ERROR\",\"impactLevel\":\"SERVICE\",\"eventType\":\"FAILURE_RATE_INCREASED\"}],\"affectedCounts\":{\"INFRASTRUCTURE\":0,\"SERVICE\":1,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"recoveredCounts\":{\"INFRASTRUCTURE\":0,\"SERVICE\":0,\"APPLICATION\":0,\"ENVIRONMENT\":0},\"hasRootCause\":true},\r\n" + 
			"\"problemDetailsText\":\"OPEN Problem 19 in environment online\\nProblem detected at: 15:54 (CST) 09.11.2020\\n\\n1 impacted service\\n\\nWeb request service\\ncrm3 (\\/ui-custsvc)\\n\\nFailure rate increase\\n216 requests\\/min impacted\\nby a failure rate increase to 10 %\\nService method: \\/ui-custsvc\\/u-route\\/custsvc\\/chgprod\\/prodRecCheck\\n\\nRoot cause\\n\\nWeb request service\\ncrm3 (\\/ui-custsvc)\\n\\nFailure rate increase\\n216 requests\\/min impacted\\nby a failure rate increase to 10 %\\nService method: \\/ui-custsvc\\/u-route\\/custsvc\\/chgprod\\/prodRecCheck\\n\\nhttps:\\/\\/192.195.1.100\\/e\\/94d81c89-3a42-4421-8030-1664ea1eb1e0\\/#problems\\/problemdetails;pid=-8847639515508209019_1604908440000V2\",\r\n" + 
			"\"receivers\":\"15069071152,13793151436,15726113306,13553158595\"\r\n" + 
			"}";

	private static String getPercVal(double d, int intRsv, int decRsv) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumIntegerDigits(intRsv);
		nf.setMinimumFractionDigits(decRsv);
		return nf.format(d);
	}

	/**
	 * The maximum width of the string buffer should reserve 4 bytes as an ellipsis,
	 * that is, MAX_LEN + 4 <= the desired limit of the number of bytes
	 * 
	 * @author Hugh
	 *
	 */
	private static final class StringBufferEx {
		private static Logger LOGGER = Logger.getLogger(StringBufferEx.class.getName());
		private final static int MAX_SIZE = 250;
		private StringBuffer buff = new StringBuffer();
		private boolean isFull = false;

		private StringBufferEx() {
			buff.append("");
		}

		private StringBufferEx(String str) {
			buff.append(str);
		}

		private void append(String str) {
			if (isFull)
				return;
			if (isFull || buff.length() >= MAX_SIZE || buff.length() + str.length() > MAX_SIZE) {
				LOGGER.severe("String length overflow!");
				isFull = true;
				buff.append(" ...");
				return;
			}
			buff.append(str);
		}

		public String toString() {
			return buff.toString();
		}
	}

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	private static String formDateTime(Long l) {
		DateTime dt = new DateTime(l);
		return fmt.print(dt);
	}

	public static void main(String[] args) throws IOException, PDUException, ResponseTimeoutException,
			InvalidResponseException, NegativeResponseException, InterruptedException {
//		SMPPWrapper.deliver("10086911", parse(jstr3), "15069071152");
//		Pattern pattern = Pattern.compile("in environment (.*)\\n");
//		String problemDetailsTxt = "RESOLVED Problem 388 in environment crm-paas-6中心\nProblem detected at: 14:46 (CST) 06.11.2020 - 15:25 (CST) 06.11.2020 (was open for 39 min)\n\n1 impacted service\n\nWeb request service\nopenchannel-* (/openchannel)\n\nFailure rate increase\n762 requests/min impacted\nby a failure rate increase to 1.23 %\nService method: All dynamic requests\n\nhttps://134.80.248.217/e/5d31d621-085e-4d60-b424-83fb8a97359a/#problems/problemdetails;pid=-2536386589880675388_1604645160000V2";
//		if (problemDetailsTxt == null || problemDetailsTxt.equals("")) {
//			LOGGER.severe("Illegal problem details text");
//		} else {
//			Matcher m = pattern.matcher(problemDetailsTxt);
//			if (m.find() && m.groupCount() >= 1) {
//				LOGGER.info(m.group(1));
//			}
//		}
		parse(jstr6);
	}

	private static String parse(String param) {
		StringBufferEx msgBuf = new StringBufferEx();
		JSONObject json = JSONObject.parseObject(param);
		if (json == null) {
			LOGGER.severe("Illegal JSON Format!");
			return null;
		}
		String sender = json.getString("sender");
		if (sender == null || sender.equals("")) {
			LOGGER.severe("Illegal sender!");
			return null;
		}
		String receivers = json.getString("receivers");
		if (receivers == null || receivers.equals("")) {
			LOGGER.severe("Illegal receivers!");
			return null;
		}
		// Problem state: OPEN or RESOLVED
		String state = json.getString("state");
		if (state == null || state.equals("")) {
			LOGGER.severe("Illegal problem state!");
			return null;
		} else {
			msgBuf.append(state + " problem ");
		}
		// Problem ID
		String problemId = json.getString("problemId");
		if (problemId == null || problemId.equals("")) {
			LOGGER.severe("Illegal problem ID!");
			return null;
		} else {
			msgBuf.append(problemId + ":");
		}
		// Problem Title
		String problemTitle = json.getString("problemTitle");
		if (problemTitle == null || problemTitle.equals("")) {
			LOGGER.severe("Illegal problem title!");
			return null;
		}
		problemTitle = problemTitle.toLowerCase();
		// Problem Details
		JSONObject problemDetails = json.getJSONObject("problemDetails");
		if (problemDetails == null) {
			LOGGER.severe("Illegal problem details!");
			return null;
		}
		// Ranked events
		JSONArray rankedEvents = null;
		rankedEvents = problemDetails.getJSONArray("rankedEvents");
		if (rankedEvents == null || rankedEvents.size() == 0) {
			LOGGER.severe("No ranked events!");
		}
		LOGGER.info("Ranked Events Count: " + rankedEvents.size());
		for (int i = 0; i < rankedEvents.size(); i++) {
			msgBuf.append(" [" + (i + 1) + "]");
			JSONObject rankedEvent = (JSONObject) rankedEvents.get(i);
			String entityName = rankedEvent.getString("entityName");
			String eventType = rankedEvent.getString("eventType");
			Long startTime = rankedEvent.getLong("startTime");
			Long endTime = rankedEvent.getLong("endTime");
			if (entityName == null || entityName.equals("") || eventType == null || eventType.equals("")
					|| startTime == null) {
				LOGGER.severe("Illegal ranked events!");
				continue;
			}
			if (eventType.equals("FAILURE_RATE_INCREASED")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + "'s " + eventType);
				JSONArray severities = rankedEvent.getJSONArray("severities");
				if (severities == null || severities.size() == 0) {
					LOGGER.severe("No severities!");
					continue;
				}
				for (int j = 0; j < severities.size(); j++) {
					JSONObject severity = (JSONObject) severities.get(j);
					Double val = severity.getDouble("value");
					if (val == null) {
						LOGGER.severe("No value!");
					}
					msgBuf.append(" to " + getPercVal(val.doubleValue(), 2, 2));
				}
			} else if (eventType.equals("HIGH_GC_ACTIVITY")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + " " + eventType);
			} else if (eventType.equals("SLOW_DISK")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				String resName = rankedEvent.getString("resourceName");
				if (resName == null || resName.equals("")) {
					LOGGER.severe("Illegal resource name!");
				}
				msgBuf.append(" " + entityName + " has " + eventType + " " + resName);
			} else if (eventType.equals("HIGH_CONNECTIVITY_FAILURES")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + " encountered " + eventType);
			}
			if (endTime == null)
				msgBuf.append(" since " + formDateTime(startTime) + ".");
			else
				msgBuf.append(" from " + formDateTime(startTime) + " to " + formDateTime(endTime) + ".");
		}
		return msgBuf.toString();
	}

	private static void sendMsg() {
//		URL url = new URL("http://localhost:8080/deliver.do");
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("POST");
//		conn.setDoOutput(true);
//		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//		dos.writeBytes(
//				"sender=" + "10086911&msg=" + URLEncoder.encode("站住！你是谁？从哪里来？要到哪里去？", "UTF-8") + "&receiver="
//						+ URLEncoder.encode("15069071152,18560059106,15069071152", "UTF-8"));
//		dos.flush();
//		dos.close();
//		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//		System.out.println(response.toString());
	}

}

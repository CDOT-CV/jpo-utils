package deduplicator;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.topologies.TimDeduplicatorTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class TimDeduplicatorTopologyTest {

    String inputTopic = "topic.OdeTimJson";
    String outputTopic = "topic.DeduplicatedOdeTimJson";

    ObjectMapper objectMapper = new ObjectMapper();

    //Original Message
    String inputTim1 = "{\"metadata\": {\"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\": {\"streamId\": \"a9012e0d-7acc-48b3-981e-aed4b4d180aa\",\"bundleSize\": 1,\"bundleId\": 113,\"recordId\": 0,\"serialNumber\": 113},\"odeReceivedAt\": \"2025-01-20T23:00:18.065Z\",\"schemaVersion\": 8,\"maxDurationTime\": 30,\"recordGeneratedAt\": \"2025-01-16T23:15:09.000Z\",\"recordGeneratedBy\": \"TMC\",\"sanitized\": false,\"odePacketID\": \"2D815A64F19A75534B\",\"odeTimStartDateTime\": \"2025-01-20T23:00:15.802Z\"},\"payload\": {\"data\": {\"msgCnt\": 1,\"timeStamp\": 22995,\"packetID\": \"2D815A64F19A75534B\",\"urlB\": \"null\",\"dataFrames\": [{\"frameType\": \"advisory\",\"msgId\": {\"roadSignID\": {\"position\": {\"lat\": 398784622,\"long\": -1050251574},\"viewAngle\": {\"from000-0to022-5degrees\": true,\"from022-5to045-0degrees\": true,\"from045-0to067-5degrees\": true,\"from067-5to090-0degrees\": true,\"from090-0to112-5degrees\": true,\"from112-5to135-0degrees\": true,\"from135-0to157-5degrees\": true,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": true,\"from225-0to247-5degrees\": true,\"from247-5to270-0degrees\": true,\"from270-0to292-5degrees\": true,\"from292-5to315-0degrees\": true,\"from315-0to337-5degrees\": true,\"from337-5to360-0degrees\": true},\"mutcdCode\": \"warning\"}},\"startYear\": 2025,\"startTime\": 28740,\"durationTime\": 30,\"priority\": 5,\"regions\": [{\"name\": \"I_US-287_SAT_1A97342A\",\"id\": {\"region\": 0,\"id\": 0},\"anchor\": {\"lat\": 398784622,\"long\": -1050251574},\"laneWidth\": 5000,\"directionality\": \"both\",\"closedPath\": false,\"direction\": {\"from000-0to022-5degrees\": false,\"from022-5to045-0degrees\": false,\"from045-0to067-5degrees\": false,\"from067-5to090-0degrees\": false,\"from090-0to112-5degrees\": false,\"from112-5to135-0degrees\": false,\"from135-0to157-5degrees\": false,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": false,\"from225-0to247-5degrees\": false,\"from247-5to270-0degrees\": false,\"from270-0to292-5degrees\": false,\"from292-5to315-0degrees\": false,\"from315-0to337-5degrees\": false,\"from337-5to360-0degrees\": false},\"description\": {\"path\": {\"scale\": 0,\"offset\": {\"ll\": {\"nodes\": [{\"delta\": {\"node-LL1\": {\"lon\": -3,\"lat\": -1349}}},{\"delta\": {\"node-LL1\": {\"lon\": 0,\"lat\": -164}}},{\"delta\": {\"node-LL4\": {\"lon\": -79,\"lat\": -36171}}},{\"delta\": {\"node-LL3\": {\"lon\": -52,\"lat\": -23867}}},{\"delta\": {\"node-LL2\": {\"lon\": 150,\"lat\": -7144}}},{\"delta\": {\"node-LL2\": {\"lon\": 936,\"lat\": -7633}}},{\"delta\": {\"node-LL3\": {\"lon\": 130,\"lat\": -15274}}},{\"delta\": {\"node-LL3\": {\"lon\": 114,\"lat\": -22432}}},{\"delta\": {\"node-LL4\": {\"lon\": -122,\"lat\": -35545}}},{\"delta\": {\"node-LL3\": {\"lon\": 378,\"lat\": -8947}}},{\"delta\": {\"node-LL3\": {\"lon\": 75,\"lat\": -9416}}}]}}}}}],\"content\": {\"workZone\": [{\"item\": {\"itis\": 1025}}]},\"url\": \"null\"}]},\"dataType\": \"us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation\"}}";
    
    // Shifted Forward 1 seconds - Should be deduplicated
    String inputTim2 = "{\"metadata\": {\"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\": {\"streamId\": \"a9012e0d-7acc-48b3-981e-aed4b4d180aa\",\"bundleSize\": 1,\"bundleId\": 113,\"recordId\": 0,\"serialNumber\": 113},\"odeReceivedAt\": \"2025-01-20T23:00:19.065Z\",\"schemaVersion\": 8,\"maxDurationTime\": 30,\"recordGeneratedAt\": \"2025-01-16T23:15:09.000Z\",\"recordGeneratedBy\": \"TMC\",\"sanitized\": false,\"odePacketID\": \"2D815A64F19A75534B\",\"odeTimStartDateTime\": \"2025-01-20T23:00:15.802Z\"},\"payload\": {\"data\": {\"msgCnt\": 1,\"timeStamp\": 22995,\"packetID\": \"2D815A64F19A75534B\",\"urlB\": \"null\",\"dataFrames\": [{\"frameType\": \"advisory\",\"msgId\": {\"roadSignID\": {\"position\": {\"lat\": 398784622,\"long\": -1050251574},\"viewAngle\": {\"from000-0to022-5degrees\": true,\"from022-5to045-0degrees\": true,\"from045-0to067-5degrees\": true,\"from067-5to090-0degrees\": true,\"from090-0to112-5degrees\": true,\"from112-5to135-0degrees\": true,\"from135-0to157-5degrees\": true,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": true,\"from225-0to247-5degrees\": true,\"from247-5to270-0degrees\": true,\"from270-0to292-5degrees\": true,\"from292-5to315-0degrees\": true,\"from315-0to337-5degrees\": true,\"from337-5to360-0degrees\": true},\"mutcdCode\": \"warning\"}},\"startYear\": 2025,\"startTime\": 28740,\"durationTime\": 30,\"priority\": 5,\"regions\": [{\"name\": \"I_US-287_SAT_1A97342A\",\"id\": {\"region\": 0,\"id\": 0},\"anchor\": {\"lat\": 398784622,\"long\": -1050251574},\"laneWidth\": 5000,\"directionality\": \"both\",\"closedPath\": false,\"direction\": {\"from000-0to022-5degrees\": false,\"from022-5to045-0degrees\": false,\"from045-0to067-5degrees\": false,\"from067-5to090-0degrees\": false,\"from090-0to112-5degrees\": false,\"from112-5to135-0degrees\": false,\"from135-0to157-5degrees\": false,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": false,\"from225-0to247-5degrees\": false,\"from247-5to270-0degrees\": false,\"from270-0to292-5degrees\": false,\"from292-5to315-0degrees\": false,\"from315-0to337-5degrees\": false,\"from337-5to360-0degrees\": false},\"description\": {\"path\": {\"scale\": 0,\"offset\": {\"ll\": {\"nodes\": [{\"delta\": {\"node-LL1\": {\"lon\": -3,\"lat\": -1349}}},{\"delta\": {\"node-LL1\": {\"lon\": 0,\"lat\": -164}}},{\"delta\": {\"node-LL4\": {\"lon\": -79,\"lat\": -36171}}},{\"delta\": {\"node-LL3\": {\"lon\": -52,\"lat\": -23867}}},{\"delta\": {\"node-LL2\": {\"lon\": 150,\"lat\": -7144}}},{\"delta\": {\"node-LL2\": {\"lon\": 936,\"lat\": -7633}}},{\"delta\": {\"node-LL3\": {\"lon\": 130,\"lat\": -15274}}},{\"delta\": {\"node-LL3\": {\"lon\": 114,\"lat\": -22432}}},{\"delta\": {\"node-LL4\": {\"lon\": -122,\"lat\": -35545}}},{\"delta\": {\"node-LL3\": {\"lon\": 378,\"lat\": -8947}}},{\"delta\": {\"node-LL3\": {\"lon\": 75,\"lat\": -9416}}}]}}}}}],\"content\": {\"workZone\": [{\"item\": {\"itis\": 1025}}]},\"url\": \"null\"}]},\"dataType\": \"us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation\"}}";
    
    // Shifted Forward 1 hour Should be allowed to pass through
    String inputTim3 = "{\"metadata\": {\"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\": {\"streamId\": \"a9012e0d-7acc-48b3-981e-aed4b4d180aa\",\"bundleSize\": 1,\"bundleId\": 113,\"recordId\": 0,\"serialNumber\": 113},\"odeReceivedAt\": \"2025-01-21T00:00:19.065Z\",\"schemaVersion\": 8,\"maxDurationTime\": 30,\"recordGeneratedAt\": \"2025-01-16T23:15:09.000Z\",\"recordGeneratedBy\": \"TMC\",\"sanitized\": false,\"odePacketID\": \"2D815A64F19A75534B\",\"odeTimStartDateTime\": \"2025-01-20T23:00:15.802Z\"},\"payload\": {\"data\": {\"msgCnt\": 1,\"timeStamp\": 22995,\"packetID\": \"2D815A64F19A75534B\",\"urlB\": \"null\",\"dataFrames\": [{\"frameType\": \"advisory\",\"msgId\": {\"roadSignID\": {\"position\": {\"lat\": 398784622,\"long\": -1050251574},\"viewAngle\": {\"from000-0to022-5degrees\": true,\"from022-5to045-0degrees\": true,\"from045-0to067-5degrees\": true,\"from067-5to090-0degrees\": true,\"from090-0to112-5degrees\": true,\"from112-5to135-0degrees\": true,\"from135-0to157-5degrees\": true,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": true,\"from225-0to247-5degrees\": true,\"from247-5to270-0degrees\": true,\"from270-0to292-5degrees\": true,\"from292-5to315-0degrees\": true,\"from315-0to337-5degrees\": true,\"from337-5to360-0degrees\": true},\"mutcdCode\": \"warning\"}},\"startYear\": 2025,\"startTime\": 28740,\"durationTime\": 30,\"priority\": 5,\"regions\": [{\"name\": \"I_US-287_SAT_1A97342A\",\"id\": {\"region\": 0,\"id\": 0},\"anchor\": {\"lat\": 398784622,\"long\": -1050251574},\"laneWidth\": 5000,\"directionality\": \"both\",\"closedPath\": false,\"direction\": {\"from000-0to022-5degrees\": false,\"from022-5to045-0degrees\": false,\"from045-0to067-5degrees\": false,\"from067-5to090-0degrees\": false,\"from090-0to112-5degrees\": false,\"from112-5to135-0degrees\": false,\"from135-0to157-5degrees\": false,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": false,\"from225-0to247-5degrees\": false,\"from247-5to270-0degrees\": false,\"from270-0to292-5degrees\": false,\"from292-5to315-0degrees\": false,\"from315-0to337-5degrees\": false,\"from337-5to360-0degrees\": false},\"description\": {\"path\": {\"scale\": 0,\"offset\": {\"ll\": {\"nodes\": [{\"delta\": {\"node-LL1\": {\"lon\": -3,\"lat\": -1349}}},{\"delta\": {\"node-LL1\": {\"lon\": 0,\"lat\": -164}}},{\"delta\": {\"node-LL4\": {\"lon\": -79,\"lat\": -36171}}},{\"delta\": {\"node-LL3\": {\"lon\": -52,\"lat\": -23867}}},{\"delta\": {\"node-LL2\": {\"lon\": 150,\"lat\": -7144}}},{\"delta\": {\"node-LL2\": {\"lon\": 936,\"lat\": -7633}}},{\"delta\": {\"node-LL3\": {\"lon\": 130,\"lat\": -15274}}},{\"delta\": {\"node-LL3\": {\"lon\": 114,\"lat\": -22432}}},{\"delta\": {\"node-LL4\": {\"lon\": -122,\"lat\": -35545}}},{\"delta\": {\"node-LL3\": {\"lon\": 378,\"lat\": -8947}}},{\"delta\": {\"node-LL3\": {\"lon\": 75,\"lat\": -9416}}}]}}}}}],\"content\": {\"workZone\": [{\"item\": {\"itis\": 1025}}]},\"url\": \"null\"}]},\"dataType\": \"us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation\"}}";
    
    // Has a different payload ID. Should be allowed through
    String inputTim4 = "{\"metadata\": {\"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\": {\"streamId\": \"a9012e0d-7acc-48b3-981e-aed4b4d180aa\",\"bundleSize\": 1,\"bundleId\": 113,\"recordId\": 0,\"serialNumber\": 113},\"odeReceivedAt\": \"2025-01-20T23:00:18.065Z\",\"schemaVersion\": 8,\"maxDurationTime\": 30,\"recordGeneratedAt\": \"2025-01-16T23:15:09.000Z\",\"recordGeneratedBy\": \"TMC\",\"sanitized\": false,\"odePacketID\": \"2D815A64F19A75534B\",\"odeTimStartDateTime\": \"2025-01-20T23:00:15.802Z\"},\"payload\": {\"data\": {\"msgCnt\": 1,\"timeStamp\": 22995,\"packetID\": \"051F23D9B7C23CF677\",\"urlB\": \"null\",\"dataFrames\": [{\"frameType\": \"advisory\",\"msgId\": {\"roadSignID\": {\"position\": {\"lat\": 398784622,\"long\": -1050251574},\"viewAngle\": {\"from000-0to022-5degrees\": true,\"from022-5to045-0degrees\": true,\"from045-0to067-5degrees\": true,\"from067-5to090-0degrees\": true,\"from090-0to112-5degrees\": true,\"from112-5to135-0degrees\": true,\"from135-0to157-5degrees\": true,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": true,\"from225-0to247-5degrees\": true,\"from247-5to270-0degrees\": true,\"from270-0to292-5degrees\": true,\"from292-5to315-0degrees\": true,\"from315-0to337-5degrees\": true,\"from337-5to360-0degrees\": true},\"mutcdCode\": \"warning\"}},\"startYear\": 2025,\"startTime\": 28740,\"durationTime\": 30,\"priority\": 5,\"regions\": [{\"name\": \"I_US-287_SAT_1A97342A\",\"id\": {\"region\": 0,\"id\": 0},\"anchor\": {\"lat\": 398784622,\"long\": -1050251574},\"laneWidth\": 5000,\"directionality\": \"both\",\"closedPath\": false,\"direction\": {\"from000-0to022-5degrees\": false,\"from022-5to045-0degrees\": false,\"from045-0to067-5degrees\": false,\"from067-5to090-0degrees\": false,\"from090-0to112-5degrees\": false,\"from112-5to135-0degrees\": false,\"from135-0to157-5degrees\": false,\"from157-5to180-0degrees\": true,\"from180-0to202-5degrees\": true,\"from202-5to225-0degrees\": false,\"from225-0to247-5degrees\": false,\"from247-5to270-0degrees\": false,\"from270-0to292-5degrees\": false,\"from292-5to315-0degrees\": false,\"from315-0to337-5degrees\": false,\"from337-5to360-0degrees\": false},\"description\": {\"path\": {\"scale\": 0,\"offset\": {\"ll\": {\"nodes\": [{\"delta\": {\"node-LL1\": {\"lon\": -3,\"lat\": -1349}}},{\"delta\": {\"node-LL1\": {\"lon\": 0,\"lat\": -164}}},{\"delta\": {\"node-LL4\": {\"lon\": -79,\"lat\": -36171}}},{\"delta\": {\"node-LL3\": {\"lon\": -52,\"lat\": -23867}}},{\"delta\": {\"node-LL2\": {\"lon\": 150,\"lat\": -7144}}},{\"delta\": {\"node-LL2\": {\"lon\": 936,\"lat\": -7633}}},{\"delta\": {\"node-LL3\": {\"lon\": 130,\"lat\": -15274}}},{\"delta\": {\"node-LL3\": {\"lon\": 114,\"lat\": -22432}}},{\"delta\": {\"node-LL4\": {\"lon\": -122,\"lat\": -35545}}},{\"delta\": {\"node-LL3\": {\"lon\": 378,\"lat\": -8947}}},{\"delta\": {\"node-LL3\": {\"lon\": 75,\"lat\": -9416}}}]}}}}}],\"content\": {\"workZone\": [{\"item\": {\"itis\": 1025}}]},\"url\": \"null\"}]},\"dataType\": \"us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation\"}}";

    @Autowired
    DeduplicatorProperties props;

    @Test
    public void testTopology() {

        props = new DeduplicatorProperties();
        props.setKafkaTopicOdeTimJson(inputTopic);
        props.setKafkaTopicDeduplicatedOdeTimJson(outputTopic);

        TimDeduplicatorTopology TimDeduplicatorTopology = new TimDeduplicatorTopology(props, null);

        Topology topology = TimDeduplicatorTopology.buildTopology();
        objectMapper.registerModule(new JavaTimeModule());

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<Void, String> inputTimData = driver.createInputTopic(
                inputTopic, 
                Serdes.Void().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, String> outputTimData = driver.createOutputTopic(
                outputTopic, 
                Serdes.String().deserializer(), 
                Serdes.String().deserializer());

            inputTimData.pipeInput(null, inputTim1);
            inputTimData.pipeInput(null, inputTim2);
            inputTimData.pipeInput(null, inputTim3);
            inputTimData.pipeInput(null, inputTim4);

            List<KeyValue<String, String>> timDeduplicatedResults = outputTimData.readKeyValuesToList();

            // validate that only 3 messages make it through
            assertEquals(3, timDeduplicatedResults.size());
            inputTim1 = inputTim1.strip();
            
            assertEquals(inputTim1.replace(" ", ""), timDeduplicatedResults.get(0).value.replace(" ", ""));
            assertEquals(inputTim3.replace(" ", ""), timDeduplicatedResults.get(1).value.replace(" ", ""));
            assertEquals(inputTim4.replace(" ", ""), timDeduplicatedResults.get(2).value.replace(" ", ""));
        
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }
}

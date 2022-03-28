/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.rcf;

import com.amazon.randomcutforest.RandomCutForest;
import com.amazon.randomcutforest.parkservices.ThresholdedRandomCutForest;
import com.amazon.randomcutforest.parkservices.state.ThresholdedRandomCutForestMapper;
import com.amazon.randomcutforest.parkservices.state.ThresholdedRandomCutForestState;
import com.amazon.randomcutforest.state.RandomCutForestMapper;
import com.amazon.randomcutforest.state.RandomCutForestState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opensearch.ml.common.parameter.BatchRCFParams;
import org.opensearch.ml.common.parameter.FitRCFParams;
import org.opensearch.ml.common.parameter.Model;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.opensearch.ml.engine.helper.MLTestHelper.TIME_FIELD;
import static org.opensearch.ml.engine.helper.MLTestHelper.constructTestDataFrame;

public class RCFModelSerDeSerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final RandomCutForestMapper rcfMapper = new RandomCutForestMapper();
    private final ThresholdedRandomCutForestMapper trcfMapper = new ThresholdedRandomCutForestMapper();

    @Test
    public void testModelSerDeSerBatchRCF() {
        BatchRCFParams params = BatchRCFParams.builder().build();
        BatchRandomCutForest batchRCF = new BatchRandomCutForest(params);
        Model model = batchRCF.train(constructTestDataFrame(500));

        RandomCutForestState deserializedState = RCFModelSerDeSer.deserializeRCF(model.getContent());
        RandomCutForest forest = rcfMapper.toModel(deserializedState);
        assertNotNull(forest);
        byte[] serializedModel = RCFModelSerDeSer.serializeRCF(deserializedState);
        assertTrue(Arrays.equals(serializedModel, model.getContent()));
    }

    @Test
    public void testModelSerDeSerFitRCF() {
        FitRCFParams params = FitRCFParams.builder().timeField(TIME_FIELD).build();
        FixedInTimeRandomCutForest fitRCF = new FixedInTimeRandomCutForest(params);
        Model model = fitRCF.train(constructTestDataFrame(500, true));

        ThresholdedRandomCutForestState deserializedState = RCFModelSerDeSer.deserializeTRCF(model.getContent());
        ThresholdedRandomCutForest forest = trcfMapper.toModel(deserializedState);
        assertNotNull(forest);
        byte[] serializedModel = RCFModelSerDeSer.serializeTRCF(deserializedState);
        assertTrue(Arrays.equals(serializedModel, model.getContent()));
    }

}

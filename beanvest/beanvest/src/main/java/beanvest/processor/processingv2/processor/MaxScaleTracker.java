package beanvest.processor.processingv2.processor;

import java.math.BigDecimal;

public class MaxScaleTracker {
    private int maxScale = 0;
    public void check(BigDecimal val)
    {
        maxScale = Math.max(val.scale(), maxScale);
    }

    public int getMaxScale()
    {
        return maxScale;
    }
}

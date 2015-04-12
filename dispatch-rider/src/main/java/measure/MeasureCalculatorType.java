package measure;

public enum MeasureCalculatorType {
    AverageDistanceFromCurLocationToBaseForAllCommissions(AverageDistanceFromCurLocationToBaseForAllCommissions.class),
    AverageDistanceFromCurLocationToBaseForUndeliveredCommissions(AverageDistanceFromCurLocationToBaseForUndeliveredCommissions.class),
    AverageDistPerCommissionAfterChange(AverageDistPerCommissionAfterChange.class),
    AverageDistPerCommissionBeforeChange(AverageDistPerCommissionBeforeChange.class),
    AverageLatencyPerCommission(AverageLatencyPerCommission.class),
    AverageLoadFromAllCommissions(AverageLoadFromAllCommissions.class),
    AverageLoadFromUndeliveredCommissions(AverageLoadFromUndeliveredCommissions.class),
    AverageMaxTimeWinSizeForAllCommissions(AverageMaxTimeWinSizeForAllCommissions.class),
    AverageMaxTimeWinSizeForUndeliveredCommissions(AverageMaxTimeWinSizeForUndeliveredCommissions.class),
    AverageMinDistBetweenAllCommissions(AverageMinDistBetweenAllCommissions.class),
    AverageMinDistBetweenUndeliveredCommissions(AverageMinDistBetweenUndeliveredCommissions.class),
    AverageMinTimeWinSizeForAllCommissions(AverageMinTimeWinSizeForAllCommissions.class),
    AverageMinTimeWinSizeForUndeliveredCommissions(AverageMinTimeWinSizeForUndeliveredCommissions.class),
    AverageNumberOfComsWithinTimeWinOfAllCommissions(AverageNumberOfComsWithinTimeWinOfAllCommissions.class),
    AverageNumberOfComsWithinTimeWinOfUndeliveredCommissions(AverageNumberOfComsWithinTimeWinOfUndeliveredCommissions.class),
    AverageTimeWindowsSizeForAllCommissions(AverageTimeWindowsSizeForAllCommissions.class),
    AverageTimeWindowsSizeForUndeliveredCommissions(AverageTimeWindowsSizeForUndeliveredCommissions.class),
    DistFromCenterOfGravityOfAllCommissionsToHolon(DistFromCenterOfGravityOfAllCommissionsToHolon.class),
    DistFromCenterOfGravityOfUndeliveredCommissionsToHolon(DistFromCenterOfGravityOfUndeliveredCommissionsToHolon.class),
    GivenCommissionsNumber(GivenCommissionsNumber.class),
    MaxLatency(MaxLatency.class),
    MaxWaitTime(MaxWaitTime.class),
    NumberOfCommissions(NumberOfCommissions.class),
    NumberOfCommissionsOthersCanAddToUsAfterChanges(NumberOfCommissionsOthersCanAddToUsAfterChanges.class),
    NumberOfCommissionsOthersCanAddToUsBeforeChanges(NumberOfCommissionsOthersCanAddToUsBeforeChanges.class),
    NumberOfCommissionsWeCanAddToOthersAfterChanges(NumberOfCommissionsWeCanAddToOthersAfterChanges.class),
    NumberOfCommissionsWeCanAddToOthersBeforeChanges(NumberOfCommissionsWeCanAddToOthersBeforeChanges.class),
    PercentageOfDelayComs(PercentageOfDelayComs.class),
    ReceivedCommissionsNumber(ReceivedCommissionsNumber.class),
    StandardDeviationAllComsFromHolon(StandardDeviationAllComsFromHolon.class),
    StandardDeviationOfDistanceFromCurLocationToBaseFromAllCommissions(StandardDeviationOfDistanceFromCurLocationToBaseFromAllCommissions.class),
    StandardDeviationOfDistanceFromCurLocationToBaseFromUndeliveredCommissions(StandardDeviationOfDistanceFromCurLocationToBaseFromUndeliveredCommissions.class),
    StandardDeviationOfLoadFromAllCommissions(StandardDeviationOfLoadFromAllCommissions.class),
    StandardDeviationOfLoadFromUndeliveredCommissions(StandardDeviationOfLoadFromUndeliveredCommissions.class),
    StandardDeviationOfMinDistBetweenAllCommissions(StandardDeviationOfMinDistBetweenAllCommissions.class),
    StandardDeviationOfMinDistBetweenUndeliveredCommissions(StandardDeviationOfMinDistBetweenUndeliveredCommissions.class),
    StandardDeviationUndeliveredComsFromHolon(StandardDeviationUndeliveredComsFromHolon.class),
    SummaryLatency(SummaryLatency.class),
    WaitTime(WaitTime.class);

    private Class<? extends MeasureCalculator> typeClass;

    MeasureCalculatorType(Class<? extends MeasureCalculator> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends MeasureCalculator> typeClass() {
        return typeClass;
    }
}

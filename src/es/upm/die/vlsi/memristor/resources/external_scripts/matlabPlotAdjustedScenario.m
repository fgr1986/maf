
function [ output_args ] = matlabPlotAdjustedScenario( simulationFileName, measuresFileName )
%Plots
%   Detailed explanation goes here
    data = load(simulationFileName);
    measures = load(measuresFileName);
    RmMeasures = measures(:,2)./measures(:,1);
    % always v-i, rm-v
    figure;plot(data(:,2),data(:,3), measures(:,1),measures(:,2));
    xlabel('V[V]');
    ylabel('I[mA]');
    legend('Simulated data', 'Measured data');
    figure;plot(data(:,2),log(abs(data(:,3))), measures(:,1),log(abs(measures(:,2))));
    xlabel('V[V]');
    ylabel('log(I)[mA]');
    legend('Simulated data', 'Measured data');
    figure;plot(data(:,2),data(:,4), measures(:,1), RmMeasures);
    xlabel('V[V]');
    ylabel('Rm[kOhms]');
    legend('Simulated data', 'Measured data');
end

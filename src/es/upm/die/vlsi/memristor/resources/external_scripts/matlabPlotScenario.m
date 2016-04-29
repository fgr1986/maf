function [ output_args ] = matlabPlotScenario( simulationFileName, columnIndex )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
    data = load(simulationFileName);
    % always v-i, rm-v
    figure;plot(data(:,2),data(:,3));
    xlabel('V[V]');
    ylabel('I[mA]');
    figure;plot(data(:,2),log(abs(data(:,3))));
    xlabel('V[V]');
    ylabel('log(I)[mA]');
    figure;plot(data(:,2),data(:,4));
    xlabel('V[V]');
    ylabel('Rm[kOhms]');
    % else 
    %for i=1:length(columIndex) 
    %    figure;plot(data(:,1), data(:,columIndex(i)));
    %end    
end

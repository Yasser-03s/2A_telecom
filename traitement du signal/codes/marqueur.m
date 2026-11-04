function [pente, Ns, P, p]= marqueur(x, methode, integration)
% MARQUEUR centre (et integre) le signal x, puis calcule la tendance (dans 
% une boucle), la retranche et calcule P(N) et trace à la fin le log-log, 
% fait la régression et renvoie la pente.
% Entrées :
%   x            : signal brut.
%   methode      :  option "1", "2", "3", "4", ou "5";
%                   1 =  blocs, moyenne.
%                   2 =  blocs, droite de regression.
%                   3 =  filtre moyenneur causal (2b).
%                   4 =  phase nulle strategie 1.
%                   5 =  phase nulle strategie 2.
%   integration  : "1" pour integration, "0" sinon.
% Sortie  :
%   pente        : pente de la courbe log-log obtenue par regression.

    %% étape 1
    x = x(:) - mean(x);
    if integration
        y = cumsum(x); 
        %cumsum() effectue la somme cumulative du vecteur (l'integration)
    else 
        y = x; 
    end
    M = length(y);
    Ns = 3:floor(M/2);
    P = zeros(size(Ns));

    %% étape 2 et 3
    for k = 1:length(Ns)
        t = tendances(y,Ns(k),methode);
        P(k) = mean((y(1:length(t)) - t)).^2;
    end
    %% étape 4

    p = polyfit(log(Ns),log(P),1);
    pente = p(1);  
    %polyfit  renvoie les coefficients deun polynôme p(x) de degré n qui 
    %est le meilleur ajustement (au sens des moindres carrés) pour les données de y
end
function [ytilde, idx] = tendance_glissante(y, N, compenser)
% TENDANCE_GLISSANTE Estime la tendance par la moyenne des N derniers
% échantillons (filtre RIF causal passe-bas, de réponse impulsionnelle
% h[j] = 1/N pour 0 <= j <= N-1).
%
% Entrées :
%   y         : signal intégré (vecteur de taille M).
%   N         : longueur de la moyenne glissante.
%   compenser : (optionnel, défaut false) si true, le retard de groupe
%               (N-1)/2 est compensé. Pour N pair, la compensation est
%               une demi-période d'échantillonnage : on moyenne les deux
%               décalages entiers voisins.
%
% Sorties :
%   ytilde : tendance estimée (taille M, de même taille que y).
%   idx    : indices où la fenêtre est entièrement contenue dans le
%            signal (pas d'effet de bord) ; P(N) est calculée sur idx.

    if nargin < 3, compenser = false; end
    y = y(:);
    M = length(y);
    g = filter(ones(1, N)/N, 1, y);         % g[k] : moyenne de y[k-N+1..k]

    if ~compenser
        ytilde = g;
        idx    = (N:M)';                    % fenêtre complète à partir de k = N
    else
        d1 = floor((N-1)/2);                % décalages entiers encadrant
        d2 = ceil((N-1)/2);                 % le retard (N-1)/2
        ytilde = nan(M, 1);
        m = (N-d1 : M-d2)';                 % indices sans effet de bord
        ytilde(m) = 0.5*(g(m+d1) + g(m+d2));% g[m+d] est centrée sur m
        idx = m;
    end
end

function [ytilde, idx] = tendance_phase_nulle_S2(y, N)
% TENDANCE_PHASE_NULLE_S2 Stratégie 2 : deux voies en parallèle,
% sortie s[n] = g[n] + r[-n], avec le filtre passe-bas du premier ordre
% h[n] = (1-a) a^n, n >= 0. La réponse globale est 2 Re{H(e^jw)}, qui est
% positive pour ce filtre : phase nulle.
%
% Entrées :
%   y : signal intégré (vecteur de taille M).
%   N : échelle de lissage. Le paramètre a est choisi pour que le retard
%       moyen de h, a/(1-a), égale celui de la moyenne glissante de
%       longueur N, soit (N-1)/2, d'où a = (N-1)/(N+1).
%
% Sorties :
%   ytilde : tendance estimée (taille M).
%   idx    : indices hors des N premiers et N derniers échantillons
%            (transitoires du filtre récursif), limités à M/4.

    y = y(:);
    M = length(y);
    a = (N-1)/(N+1);                        % 0 < a < 1 pour N >= 2
    g = filter(1-a, [1 -a], y);             % voie directe : g = h * y
    r = filter(1-a, [1 -a], flip(y));       % voie retournée : r = h * y[-n]
    ytilde = (g + flip(r)) / 2;             % s[n] = g[n] + r[-n], gain
                                            % statique 2H(1) = 2 normalisé
    n0  = min(N, floor(M/4));               % transitoires aux deux bords
    idx = (n0 : M-n0+1)';
end

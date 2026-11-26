function [ytilde, idx] = tendance_phase_nulle_S1(y, N)
% TENDANCE_PHASE_NULLE_S1 Stratégie 1 : filtrage, retournement,
% filtrage, retournement, avec h[n] = 1/N sur 0 <= n <= N-1.
% La réponse globale est H(z)H(1/z), soit |H(e^jw)|^2 : phase nulle.
%
% Entrées :
%   y : signal intégré (vecteur de taille M).
%   N : longueur du filtre moyenneur h.
%
% Sorties :
%   ytilde : tendance estimée, alignée sur y (taille M).
%   idx    : indices sans effet de bord (N : M-N+1). Le signal est
%            supposé nul hors de [1, M] pour les indices voisins des bords.
%
% Les convolutions sont complètes (conv) : les suites ne sont pas
% tronquées avant les retournements, sinon la symétrie, donc la phase
% nulle, serait perdue.

    y = y(:);
    M = length(y);
    h = ones(N, 1)/N;
    g = conv(h, y);                         % g = h * y          (M+N-1)
    r = conv(h, flip(g));                   % r = h * g[-n]      (M+2N-2)
    s = flip(r);                            % s[n] = r[-n]
    ytilde = s(N : N+M-1);                  % recalage : le centre de la
                                            % réponse globale est en N
    idx = (N : M-N+1)';
end

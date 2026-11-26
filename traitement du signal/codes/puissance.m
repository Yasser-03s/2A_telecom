function P = puissance(y, ytilde, idx)
% PUISSANCE Puissance (valeur quadratique moyenne) du processus résiduel
% r[k] = y[k] - ytilde[k], calculée sur les indices idx.
%
% Entrées :
%   y      : signal intégré (vecteur).
%   ytilde : tendance estimée (même taille que y, ou taille length(idx)
%            si elle est définie uniquement sur idx = 1:length(ytilde)).
%   idx    : indices sur lesquels la puissance est moyennée.
%
% Sortie :
%   P : P(N) = (1/|idx|) * somme_{k dans idx} |y[k] - ytilde[k]|^2.

    y = y(:);  ytilde = ytilde(:);
    r = y(idx) - ytilde(idx);               % processus résiduel
    P = mean(abs(r).^2);
end

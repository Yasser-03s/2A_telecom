function [ytilde, idx] = tendance_blocs(y, N, p)
% TENDANCE_BLOCS Estime la tendance de y par un polynôme de degré p
% ajusté au sens des moindres carrés sur des blocs sans recouvrement.
%
% Entrées :
%   y : signal intégré (vecteur de taille M).
%   N : nombre d'échantillons par bloc (N >= p+1).
%   p : degré du polynôme (0 : moyenne, 1 : droite de régression, etc.).
%
% Sorties :
%   ytilde : tendance concaténée des blocs (taille B*N, B = floor(M/N)).
%   idx    : indices de y sur lesquels la tendance est définie, 1:B*N.
%
% Le vecteur des coefficients du bloc est Theta = (H'H)^(-1) H' y_bloc.
% Il est calculé avec l'opérateur \ (factorisation QR), mathématiquement
% équivalent mais mieux conditionné que l'inversion de H'H.

    y = y(:);
    M = length(y);
    B = floor(M/N);                         % nombre de blocs
    k = (1:N)';                             % indice local dans un bloc
    H = (k/N) .^ (0:p);                     % matrice de Vandermonde N x (p+1)
                                            % (k/N au lieu de k : même espace
                                            % engendré, donc même tendance, mais
                                            % conditionnement meilleur)
    Yblocs = reshape(y(1:B*N), N, B);       % une colonne par bloc
    Theta  = H \ Yblocs;                    % moindres carrés, tous les blocs
    ytilde = reshape(H * Theta, B*N, 1);    % concaténation des tendances
    idx    = (1:B*N)';
end

function [alpha, beta, Nvals, P] = marqueur(y, methode, Nvals)
% MARQUEUR Calcule P(N) pour plusieurs échelles N, puis la droite de
% régression de ln P(N) en fonction de ln N. Sa pente alpha est le
% marqueur.
%
% Entrées :
%   y       : signal intégré (ou centré seulement, selon l'étude).
%   methode : handle @(y,N) -> [ytilde, idx] (une des fonctions tendance_*).
%   Nvals   : échelles N testées (vecteur d'entiers).
%
% Sorties :
%   alpha, beta : pente et ordonnée à l'origine de ln P = alpha ln N + beta.
%   Nvals       : échelles effectivement utilisées (celles où P > 0).
%   P           : puissances résiduelles P(N) associées.

    P = zeros(size(Nvals));
    for i = 1:length(Nvals)
        [ytilde, idx] = methode(y, Nvals(i));
        P(i) = puissance(y, ytilde, idx);
    end

    valide = P > eps;                       % exclut les ajustements exacts
    Nvals  = Nvals(valide);                 % (résidu nul, ln(0) impossible)
    P      = P(valide);

    u = log(Nvals(:));                      % abscisses ln N
    v = log(P(:));                          % ordonnées ln P(N)
    coef  = polyfit(u, v, 1);               % moindres carrés, p = 1
    alpha = coef(1);
    beta  = coef(2);
end

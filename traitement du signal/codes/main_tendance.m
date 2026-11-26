%% Section 2.4 : extraction du marqueur par retrait de tendance
% Cas 1 : bruit blanc gaussien centré ; Cas 2 : processus AR(2).
% Pour chaque cas et chaque méthode de tendance :
%   étape 1 (centrage, intégration) -> étape 2 (tendance) ->
%   étape 3 (puissance résiduelle P(N)) -> étape 4 (pente log-log).
% Fichiers produits : figures PNG et tableaux LaTeX inclus dans le rapport.
clear; close all; clc;
rng(1);                                     % reproductibilité

%% Paramètres généraux
M        = 2000;                            % nombre d'échantillons
variance = 1;                               % variance du bruit blanc
Q_ar2    = [1 -1.5 0.8];                    % AR(2) : pôles de module sqrt(0.8)
N0       = 100;                             % échelle pour visualiser les tendances
nbReal   = 30;                              % réalisations (Monte-Carlo)

% Échelles N de 3 à M/2, réparties de façon régulière en échelle log
Nvals = unique(round(logspace(log10(3), log10(M/2), 40)));

%% Définition des méthodes (nom, handle, N minimal)
methodes = {
    'Blocs p=0 (moyenne)',    @(y,N) tendance_blocs(y,N,0),           3
    'Blocs p=1 (droite)',     @(y,N) tendance_blocs(y,N,1),           3
    'Blocs p=2 (parabole)',   @(y,N) tendance_blocs(y,N,2),           4
    'Moyenne glissante',      @(y,N) tendance_glissante(y,N,false),   3
    'Glissante compensée',    @(y,N) tendance_glissante(y,N,true),    3
    'Phase nulle S1',         @(y,N) tendance_phase_nulle_S1(y,N),    3
    'Phase nulle S2',         @(y,N) tendance_phase_nulle_S2(y,N),    3};
nbMeth = size(methodes, 1);

%% Génération des deux cas
b   = sqrt(variance) * randn(1, M);
cas = {'bruit blanc', 'AR(2)'};
sig = {b, filter(1, Q_ar2, b)};

alphas = zeros(nbMeth, 2);                  % marqueurs (méthode x cas)

for c = 1:2
    x = sig{c}(:);
    y = integration(x, true);               % étape 1

    % --- Vérification 1 : signal, signal intégré et tendances pour N0
    figure('Name', ['Tendances - ' cas{c}]);
    subplot(2,1,1); plot(x); title(['Signal x[k] : ' cas{c}]);
    subplot(2,1,2); plot(y, 'Color', [0.6 0.6 0.6]); hold on;
    couleurs = lines(nbMeth);
    for m = 1:nbMeth
        [yt, idx] = methodes{m,2}(y, N0);
        plot(idx, yt(idx), 'Color', couleurs(m,:), 'DisplayName', methodes{m,1});
    end
    hold off; legend('Signal intégré', methodes{:,1}, 'Location', 'best');
    title(['Signal intégré et tendances estimées (N = ' num2str(N0) ')']);
    xlabel('k');
    saveas(gcf, ['tendances_' num2str(c) '.png']);

    % --- Marqueur et représentation log-log pour chaque méthode
    figure('Name', ['Log-log - ' cas{c}]); hold on;
    for m = 1:nbMeth
        Nm = Nvals(Nvals >= methodes{m,3});
        [alpha, beta, Nu, P] = marqueur(y, methodes{m,2}, Nm);
        alphas(m,c) = alpha;
        loglog(Nu, P, 'o', 'Color', couleurs(m,:), 'HandleVisibility', 'off');
        loglog(Nu, exp(beta)*Nu.^alpha, '-', 'Color', couleurs(m,:), ...
               'DisplayName', sprintf('%s (\\alpha = %.2f)', methodes{m,1}, alpha));
    end
    hold off; set(gca, 'XScale', 'log', 'YScale', 'log'); grid on;
    xlabel('N'); ylabel('P(N)'); legend('Location', 'northwest');
    title(['P(N) et droites de régression : ' cas{c}]);
    saveas(gcf, ['loglog_' num2str(c) '.png']);
end

%% Tableau des marqueurs (affichage + fichier LaTeX)
fprintf('\n%-24s %12s %12s\n', 'Méthode', cas{1}, cas{2});
for m = 1:nbMeth
    fprintf('%-24s %12.3f %12.3f\n', methodes{m,1}, alphas(m,1), alphas(m,2));
end
ecrire_tableau('tab_marqueurs.tex', methodes(:,1), alphas);

%% Étude 1 : suppression de l'intégration (étape 1)
alphas_ni = zeros(nbMeth, 2);
for c = 1:2
    x = sig{c}(:);
    y = integration(x, false);              % centrage seul
    for m = 1:nbMeth
        Nm = Nvals(Nvals >= methodes{m,3});
        alphas_ni(m,c) = marqueur(y, methodes{m,2}, Nm);
    end
end
fprintf('\nSans intégration :\n');
for m = 1:nbMeth
    fprintf('%-24s %12.3f %12.3f\n', methodes{m,1}, alphas_ni(m,1), alphas_ni(m,2));
end
ecrire_tableau('tab_sans_integration.tex', methodes(:,1), alphas_ni);

%% Étude 2 : dépendance au nombre d'échantillons M (moyenne sur nbReal réalisations)
Mliste = [500 1000 2000 5000];
sel    = [1 2 4 6 7];                       % méthodes comparées
moy = zeros(length(sel), length(Mliste), 2);
ect = zeros(size(moy));
for c = 1:2
    for im = 1:length(Mliste)
        Mi = Mliste(im);
        Ni = unique(round(logspace(log10(3), log10(Mi/2), 40)));
        a  = zeros(length(sel), nbReal);
        for r = 1:nbReal
            bi = sqrt(variance) * randn(1, Mi);
            if c == 1, xi = bi; else, xi = filter(1, Q_ar2, bi); end
            yi = integration(xi(:), true);
            for s = 1:length(sel)
                Nm = Ni(Ni >= methodes{sel(s),3});
                a(s,r) = marqueur(yi, methodes{sel(s),2}, Nm);
            end
        end
        moy(:,im,c) = mean(a, 2);
        ect(:,im,c) = std(a, 0, 2);
    end
end
ecrire_tableau_M('tab_dependance_M.tex', methodes(sel,1), Mliste, moy, ect);

%% Étude 3 : dépendance à la plage de N (M = 2000, une réalisation moyennée)
plages = [3 30; 30 300; 3 1000];            % [Nmin Nmax]
a3 = zeros(size(plages,1), 2);
for c = 1:2
    for ip = 1:size(plages,1)
        Np = Nvals(Nvals >= plages(ip,1) & Nvals <= plages(ip,2));
        acc = zeros(1, nbReal);
        for r = 1:nbReal
            bi = sqrt(variance) * randn(1, M);
            if c == 1, xi = bi; else, xi = filter(1, Q_ar2, bi); end
            acc(r) = marqueur(integration(xi(:), true), methodes{2,2}, Np);
        end
        a3(ip,c) = mean(acc);
    end
end
fprintf('\nPlages de N (blocs p=1) :\n'); disp([plages a3]);
fid = fopen('tab_plages.tex', 'w', 'n', 'UTF-8');
fprintf(fid, '\\begin{tabular}{lcc}\n\\toprule\n');
fprintf(fid, 'Plage de $N$ & Bruit blanc & AR(2) \\\\\n\\midrule\n');
for ip = 1:size(plages,1)
    fprintf(fid, '$[%d,%d]$ & %.2f & %.2f \\\\\n', plages(ip,1), plages(ip,2), a3(ip,1), a3(ip,2));
end
fprintf(fid, '\\bottomrule\n\\end{tabular}\n');
fclose(fid);

%% ------------------------------------------------------------------------
function y = integration(x, integrer)
% INTEGRATION Étape 1 : centrage puis, si integrer = true, intégration.
%   x : signal (vecteur) ; integrer : booléen. Sortie : y[k].
    xc = x - mean(x);
    if integrer, y = cumsum(xc); else, y = xc; end
end

function ecrire_tableau(nomFichier, noms, alphas)
% ECRIRE_TABLEAU Écrit un tabular LaTeX complet (méthode, alpha cas 1, alpha cas 2).
    fid = fopen(nomFichier, 'w', 'n', 'UTF-8');
    fprintf(fid, '\\begin{tabular}{lcc}\n\\toprule\n');
    fprintf(fid, 'Méthode de tendance & Bruit blanc & AR(2) \\\\\n\\midrule\n');
    for m = 1:length(noms)
        fprintf(fid, '%s & %.2f & %.2f \\\\\n', noms{m}, alphas(m,1), alphas(m,2));
    end
    fprintf(fid, '\\bottomrule\n\\end{tabular}\n');
    fclose(fid);
end

function ecrire_tableau_M(nomFichier, noms, Mliste, moy, ect)
% ECRIRE_TABLEAU_M Écrit un tabular LaTeX complet : moyenne +- écart-type de alpha
% selon M, bruit blanc (haut) puis AR(2) (bas).
    fid = fopen(nomFichier, 'w', 'n', 'UTF-8');
    fprintf(fid, '\\begin{tabular}{lcccc}\n\\toprule\n');
    fprintf(fid, 'Méthode');
    for im = 1:length(Mliste), fprintf(fid, ' & $M=%d$', Mliste(im)); end
    fprintf(fid, ' \\\\\n\\midrule\n');
    for c = 1:2
        for s = 1:length(noms)
            fprintf(fid, '%s', noms{s});
            for im = 1:length(Mliste)
                fprintf(fid, ' & $%.2f \\pm %.2f$', moy(s,im,c), ect(s,im,c));
            end
            fprintf(fid, ' \\\\\n');
        end
        if c == 1, fprintf(fid, '\\midrule\n'); end
    end
    fprintf(fid, '\\bottomrule\n\\end{tabular}\n');
    fclose(fid);
end
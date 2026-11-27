function c = cc_encode(u, trellis)
% cc_encode: fonction qui prend en paramètre un vecteur de K symboles et
% une structure de trellis et renvoie le code à la sortie

%% exemple de test: 
% u= [1,0,1,1]
% trellis= poly2trellis(3,[7,5],7) (pour encodeur (1,7/5)_8)
% cc_encode(u,trellis)
%%
    nextStates = trellis.nextStates;
    outputs    = trellis.outputs;
    m  = log2(trellis.numStates);
    ns = log2(trellis.numOutputSymbols);
    K  = length(u);
    L  = K + m;
    c  = zeros(1, ns*L);
    state = 0;
    for i = 1:L
        if i <= K
            in = u(i);
        else
            % entrée de fermeture : celle qui mène à l'état suivant le plus petit
            if nextStates(state+1, 1) < nextStates(state+1, 2)
                in = 0;
            else
                in = 1;
            end
        end
        sortie = int2bit(outputs(state+1, in+1), ns).';
        c(ns*(i-1)+1 : ns*i) = sortie;
        state = nextStates(state+1, in+1);
    end
end
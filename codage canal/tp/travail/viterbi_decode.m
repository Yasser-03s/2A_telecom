function u = viterbi_decode(y, trellis)
% viterbi_decode: fonction qui prend en paramètre un vecteur de ns.L observations et
% une structure de trellis et renvoie le code à l'entrée

%% exemple de test: 
% u= [1,0,1,1]
% trellis= poly2trellis(3,[7,5],7) (pour encodeur (1,7/5)_8)
% cc_encode(u,trellis)
%%
    nextStates = trellis.nextStates;
    outputs    = trellis.outputs;
    nb_etats   = trellis.numStates;
    m  = log2(nb_etats);
    ns = log2(trellis.numOutputSymbols);
    nb_etapes = length(y)/ns;

    J          = Inf(nb_etats, nb_etapes+1);
    prevStates = zeros(nb_etats, nb_etapes+1);
    prevBit    = zeros(nb_etats, nb_etapes+1);
    Y = reshape(y(:), ns, nb_etapes).';   % Y(n,:) = y_n
    J(1,1) = 0;

    for n = 1:nb_etapes
        for etat = 1:nb_etats
            if J(etat,n) == Inf
                continue;
            end
            for bit = [0,1]
                next_etat = nextStates(etat,bit+1) + 1;
                out  = outputs(etat,bit+1);
                bits_out = bitget(out, ns:-1:1);   % entier -> ns bits
                c = 1 - 2*bits_out;                % 0 -> +1, 1 -> -1
                cout_branche = -(c * Y(n,:).');    % -c . y_n^T
                candidat = J(etat,n) + cout_branche;
                if candidat < J(next_etat,n+1)
                    J(next_etat,n+1)          = candidat;
                    prevStates(next_etat,n+1) = etat;
                    prevBit(next_etat,n+1)    = bit;
                end
            end
        end
    end

    bits = zeros(1, nb_etapes);
    s = 1;                                   % état final = 0
    for n = nb_etapes:-1:1
        bits(n) = prevBit(s, n+1);
        s       = prevStates(s, n+1);
    end
    u = bits(1:nb_etapes - m);
end
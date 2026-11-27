function c = cc_encode(u, trellis)
% cc_encode: fonction qui prend en paramètre un vecteur de K symboles et
% une structure de trellis et renvoie 1 sortie

%% exemple de test: 
% u= [1,0,1,1]
% trellis= poly2trellis(3,[7,5],7) (pour encodeur (1,7/5)_8)
% cc_encode(u,trellis)
%%
    numInputSymbols= trellis.numInputSymbols;
    nextStates= trellis.nextStates;
    outputs= trellis.outputs;    
    m= log2(trellis.numStates);
    ns= log2(trellis.numOutputSymbols);
    K= length(u);
    L= m+K;
    c= [];
    state= 0;
    for i=1:K
        sortie= int2bit(outputs(state+1,u(i)+1), ns).';
        c=[c, sortie];
        state= nextStates(state+1,u(i)+1);
    end
    
    
    while state ~= 0
        
        if nextStates(state+1,0+1)<state
            sortie= int2bit(outputs(state+1, 0+1),ns).';          
            state= nextStates(state+1,0+1);
        else
            sortie= int2bit(outputs(state+1, 1+1),ns).';
            state= nextStates(state+1,1+1);
        end
        c=[c,sortie];
    end
    len= length(c)
    nsL=ns*L
end

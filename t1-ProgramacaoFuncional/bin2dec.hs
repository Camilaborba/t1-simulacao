-- Definir uma função recursiva que recebe um número binário (interpretado como número inteiro sem sinal)
-- e retorna o valor equivalente em decimal. bin2dec :: [Int] -> Int


-- Assinatura da função 
bin2dec :: [Int] -> Int

-- Implementação da função
bin2dec = foldl (\acc x -> acc * 2 + x) 0

-- Exemplo de uso no programa principal
main :: IO ()
main = do
    print $ bin2dec [1, 0, 1, 1] -- Deve retornar 11
    print $ bin2dec [1, 1, 0, 1, 0] -- Deve retornar 26
    print $ bin2dec [1, 0, 0, 1] -- Deve retornar 9

-- Converte um número inteiro decimal para uma lista binária.
decimalParaBinario :: Int -> [Int]
decimalParaBinario 0 = [0]
decimalParaBinario 1 = [1]
decimalParaBinario n = decimalParaBinario (n `div` 2) ++ [n `mod` 2]

-- Ajusta o tamanho da lista binária para um número específico de bits.
ajustarBits :: Int -> [Int] -> [Int]
ajustarBits n bits = let
    len = length bits
    zeros = replicate (n - len) 0
  in if len < n then zeros ++ bits  -- Adiciona zeros se for muito curto
     else take n (drop (len - n) bits)  -- Trunca se for muito longo

-- Função principal que converte decimal para binário com número de bits fixo.
decimalParaBinarioFixo :: Int -> Int -> [Int]
decimalParaBinarioFixo n bits
  | n < 0 = error "Número negativo não é suportado"
  | otherwise = ajustarBits bits (decimalParaBinario n)

-- Exemplo de uso no programa principal
main :: IO ()
main = do
    print $ decimalParaBinarioFixo (-1) 3 -- Deve retornar 11


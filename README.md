# FileCompressor
## *File compression and decompression using Huffman Algorithm*

## Usage
### First - make a compression object:
```java
HuffmanCompression test = new HuffmanCompression();
```
### Compression: 
```java
test.compressFile((HashMap<Character, String>) computeCodes(makeCodeTree((HashMap<Character, Long>) countFrequencies(pathName))), pathName, pathNameCompressed); //Where pathName is current file and pathNameCompressed is the desired filename
```
### Decompression:
```java
test.decompressFile(pathNameCompressed, pathNameDecompressed, makeCodeTree((HashMap<Character, Long>) countFrequencies(pathName))); //Where pathNameCompressed is current file and pathNameDecompressed is the desired filename
```

## Notes
+ feel free to use, it is 0-loss and reasonably efficient
+ Update: allow for command line path arguments (and compression vs decompression, not just both)
+ pull requests welcome!

Education-Restricted License 2024, Colin Wolfe

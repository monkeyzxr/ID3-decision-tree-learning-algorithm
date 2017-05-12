# ID3-decision-tree-learning-algorithm
a machine learning program about building a binary decision tree classifier using the ID3 algorithm

****************************************************
Authors: Xiangru Zhou and Xunde Wang 
****************************************************
Project Requirement:

You will implement the ID3 decision tree learning algorithm using any one of the following programming languages - Java, Python, C++, Ruby. You cannot use any package or library for this assignment.

To simplify things, you can assume that the data used to test your implementation will contain only Boolean (0 or 1) attributes and Boolean (0 or 1) class values. 

You can assume that there will be no missing data or attributes. 

You can also assume that the first row of the dataset will contain column names and each non-blank line after that will contain a new data instance.

Your program should read three arguments from the command line – complete path of the training dataset, complete path of the test dataset, and the pruning factor.

Your program should contain a print method that should output the current tree to the screen.

After reading all the data instances, you should output a summary of the datasets, and compute the pre-pruned accuracy on the training data and also accuracy of the model on the test dataset and output them to the screen. You should also output the plot of the decision tree model. 

After the decision tree has been constructed, you will check the pruning factor, which will be given by the third argument to your program. The pruning factor is defined as the fraction of the nodes that you will prune.

After pruning the tree, you will re-compute the training and test accuracy and output the summary on the screen as before. You should also output the plot of the post-pruned decision tree model.

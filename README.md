1. Create docker image with maven plugin
```
mvn clean install
```

2. Tag and push the image to DockerHub repository (make it public)
```
docker login
docker tag <image-id> <your-dockerhub-name>/<your-repo>:<tag>
docker push <your-dockerhub-name>/<your-repo>:<tag>
```

3. Deploy your Azure infrastructure with terraform
```
terraform init
terraform plan
terraform apply
```

4. Set your `kubectl` to Azure cluster 
```
az aks get-credentials -g <resource-group-name> -n <cluster-name>
```

5. Launch Spark app in AKS cluster
```
spark-submit \
    --master k8s://https://<k8s-apiserver-host>:<k8s-apiserver-port> \
    --deploy-mode cluster \
    --name sparkbasics \
    --class App \
    --num-executors 1 \
    --conf spark.driver.cores=1 --conf spark.driver.memory=2g \
    --conf spark.executor.cores=1 --conf spark.executor.memory=2g \
    --conf spark.kubernetes.container.image=<your-dockerhub-name>/<your-repo>:<tag> \
    --conf spark.openCage.apiKey=<your-opencage-api-key> \
    --conf spark.azure.abfssPath=abfss://<input-container-name>@<input-storage-account-name>.dfs.core.windows.net/ \
    --conf spark.azure.accountName=<account-name> \
    --conf spark.azure.clientId=<client-id> \
    --conf spark.azure.secret=<secret> \
    --conf spark.azure.endpoint=<endpoint> \
    --conf spark.azure.outputPath=abfss://<output-container-name>@<output-storage-account-name>.dfs.core.windows.net/result \
    --conf spark.hadoop.fs.azure.account.key.<output-storage-account-name>.dfs.core.windows.net=<output-storage-account-access-key> \
    local:///opt/sparkbasics-1.0.0.jar
```

Custom app configs include:
- `spark.azure.abfssPath` - path for weather and hotels data container
- `spark.openCage.apiKey` - OpenCage API key, granted after registration on `https://opencagedata.com/`
- `spark.azure.outputPath` - output directory for result in azure storage account container
- `spark.azure.accountName` - input data account name
- `spark.azure.clientId` - input data clientId name
- `spark.azure.secret` - input data secret
- `spark.azure.endpoint` - input data endpoint

6. Check results and delete Azure infrastructure with

```
terraform destroy
```

<br/>

Optional:
To test spark-submit locally with minikube:
1. Download minikube
   `https://minikube.sigs.k8s.io/docs/start/`
1. `minikube start`
2. `kubectl proxy`
3. Change master url to proxy  `k8s://http://127.0.0.1:8001`
4. Launch Spark app in local k8s with updated spark-submit above
4. Optional: `minikube dashboard` - access minikube UI

<br/>

Useful links:
- https://developer.hashicorp.com/terraform/tutorials/azure-get-started/azure-build?in=terraform%2Fazure-get-started
- https://spark.apache.org/docs/latest/running-on-kubernetes.html

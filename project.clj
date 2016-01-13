(defproject sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [instaparse "1.4.1"]]
  :resource-paths ["lib/asm-5.0.4.jar" "lib/asm-analysis-5.0.4.jar" "lib/asm-commons-5.0.4.jar" "lib/asm-tree-5.0.4.jar" "lib/asm-util-5.0.4.jar" "lib/asm-xml-5.0.4.jar"]
  :java-source-paths ["src"])

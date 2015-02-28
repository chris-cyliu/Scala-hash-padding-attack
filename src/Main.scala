/**
 * Created by Chris on 28/2/15.
 */

import scalaj.http.Http
import sys.process._

object Main {

  val max_loop = 100;

  val data = "data"

  val append_data = "attacking"

  val org_hash = "4d79fa9cbd624b44ff02df898fda3610d89fed43"

  val exec = "/Users/Chris/Documents/comp444/src/hash_extender"

  val request_url = "http://hacker-heart.appspot.com/comp444/assign.php"

  /**
   * loop though 1 - 100
   * call the hash_extender program
   *
   * get "New signature:"
   * get "New string:"
   *
   * construct url
   *
   * http request
   *
   * if response correct return i
   * @param args
   */
  def main(args:Array[String]) :Unit = {
    try {
      1 to max_loop foreach {
        i =>
          println(s"Try byte : $i")
          val cmd = s"$exec -d $data -a $append_data -s $org_hash -f sha1 --out-data-format=html -l $i"
          println(cmd)
          val output = cmd.!!.split("\n")
          output.map(println(_))

          val hash = output(2).split(":")(1).substring(1)
          val new_data = output(3).split(":")(1).substring(1)

          var success = false
          while (!success)
            try {
              val request = Http(s"$request_url?d=$new_data&h=$hash")
              var http_ret = request.asString
              println(request.getUrl)

              success = true
              println(http_ret)
              if(http_ret.contains("is correct")){
                throw Found(i)
              }

            } catch {
              case a:Found => throw a
              case exception: Exception =>
            }

      }
    }catch{
      case a:Found => "Correct secret bit :"+a.ret
    }

  }
}


case class Found(ret:Int) extends Exception

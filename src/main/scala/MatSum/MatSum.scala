package matsum

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}
import scala.collection.mutable.ArrayBuffer

class MatSum extends Module {
  val io = IO(new Bundle {
    val ins   = Input(Vec(4, UInt(32.W)))
    val load  = Input(Bool())
    val out = Output(UInt(32.W))
  })
  var sum = UInt(32.W)
  val buffer = new ArrayBuffer[UInt]()
  for(i <- 0 until 4) {                
		buffer += io.ins(i)
  	}
  sum = 0.asUInt(32.W)
  for(i <- 0 until 4) {
     sum = sum + buffer(i)
  }
  
  io.out := sum
  
  
}

class MatTests(c: MatSum) extends PeekPokeTester(c) {
  var sum = 0
  for (i <- 0 until 4)
	poke(c.io.ins(i), 0)
  poke(c.io.load, 0)
  step (1)
  var i = UInt(32.W)
  for (i <- 0 until 4)
	poke(c.io.ins(i), i+4)
  poke(c.io.load, 1)
  step(1)
  expect(c.io.out, 22)
}

object MatSum {
  def main(args: Array[String]): Unit = {
    if (!Driver(() => new MatSum())(c => new MatTests(c))) System.exit(1)
  }
}
